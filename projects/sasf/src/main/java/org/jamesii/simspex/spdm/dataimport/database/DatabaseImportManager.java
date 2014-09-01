/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.ProblemPerformanceTuple;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IFeature;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.features.FeatureExtraction;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.util.PerfDB;
import org.jamesii.simspex.gui.SimSpExPerspective;

/**
 * Data importer for accessing an {@link IPerformanceDatabase}. It assumes that
 * all applications have the same features, i.e. the same set of
 * {@link FeatureExtractorFactory} implementations is applicable to them. The
 * {@link org.jamesii.perfdb.recording.selectiontrees.SelectionTree} instances
 * are converted into {@link Configuration} instances by flattening.
 * 
 * @author Roland Ewald
 * 
 */
public class DatabaseImportManager implements
    IDMDataImportManager<ProblemPerformanceTuple> {

  /**
   * String to match all URI parts of those problem schemes whose performance
   * should be learned.
   */
  private final String targetProblemSchemeURIPart;

  /** String to match the performance measurement to be used. */
  private final String targetPerformance;

  /**
   * This factor determines the share of the maximal amount of
   * {@link IRuntimeConfiguration} instances that have to be available for a
   * given {@link IProblemDefinition} for its inclusion into the data.
   */
  private double rtConfigCoverageFactor = 1;

  /** The target {@link IPerformanceType}. */
  private IPerformanceType targetPerfType;

  /** The factory associated with the target performance type. */
  private PerformanceMeasurerFactory targetPerfMeasurerFactory;

  /** Reference to the {@link IPerformanceDatabase}. */
  private IPerformanceDatabase perfDB;

  /**
   * A map containing all class names of the available
   * {@link FeatureExtractorFactory} implementations and their corresponding
   * {@link IFeatureType} instances.
   */
  private Map<String, IFeatureType> featureMap;

  /**
   * List of factories for feature extraction. Will be the same for all models
   * to be analysed.
   */
  private List<FeatureExtractorFactory> extractorFactories = null;

  /**
   * Flag to determine whether feature extractors should be applied during
   * import. Note that this may *change* the database (by adding missing
   * features).
   */
  private boolean applyingFeatureExtraction = true;

  /**
   * The black list. Parts of factory names. Any configuration that contains a
   * factory with a matching name will be ignored.
   */
  private List<String> blackList = new ArrayList<>();

  /**
   * Default constructor.
   * 
   * @param targetProblemName
   *          part of the target {@link java.net.URI} of the problem schemes to
   *          be considered
   * @param targetPerfMeasureFac
   *          the target performance measurement name (factory)
   * @param performanceDatabase
   *          the performance database from which the data shall be imported
   */
  public DatabaseImportManager(String targetProblemName,
      String targetPerfMeasureFac, IPerformanceDatabase performanceDatabase) {
    targetProblemSchemeURIPart = targetProblemName;
    targetPerformance = targetPerfMeasureFac;
    perfDB = performanceDatabase;
    try {
      perfDB.open();
      featureMap = PerfDB.getFeatureMap(perfDB);
      FeatureExtraction.refreshFeatureExtractors(perfDB);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "Processing features from database failed.", ex);
    }
    retrievePerfType();
  }

  /**
   * Retrieves desired {@link IPerformanceType} from the
   * {@link IPerformanceDatabase}.
   * 
   * @return the desired {@link IPerformanceType}
   */
  private void retrievePerfType() {
    PerformanceMeasurerFactory targetPerfFactory = findPerfMeasurerFactory();
    SimSystem.report(Level.INFO, "Retrieving performance type for:"
        + targetPerfFactory.getClass());
    targetPerfType = perfDB.getPerformanceType(targetPerfFactory.getClass());
    targetPerfMeasurerFactory = targetPerfFactory;
  }

  /**
   * Find performance measurer factory.
   * 
   * @return the performance measurer factory
   */
  public final PerformanceMeasurerFactory findPerfMeasurerFactory() {
    PerformanceMeasurerFactory targetPerfFactory = null;
    List<PerformanceMeasurerFactory> factories =
        SimSystem.getRegistry().getFactories(PerformanceMeasurerFactory.class);
    for (PerformanceMeasurerFactory factory : factories) {
      if (factory.getName().contains(targetPerformance)) {
        targetPerfFactory = factory;
        break;
      }
    }

    if (targetPerfFactory == null) {
      throw new IllegalStateException(
          "Could not find a performance measurer facctory that contains the string '"
              + targetPerformance + "'.");
    }
    return targetPerfFactory;
  }

  @Override
  public PerformanceDataSet<ProblemPerformanceTuple> getPerformanceData() {

    if (targetPerfType == null) {
      throw new IllegalStateException(
          "Could not find a performance type for target performance measure '"
              + targetPerformance + "'.");
    }

    List<ProblemPerformanceTuple> data = new ArrayList<>();
    perfDB = SimSpExPerspective.getPerformanceDataBase();

    try {
      perfDB.open();
      List<IProblemScheme> allModels = perfDB.getAllProblemSchemes();
      for (IProblemScheme model : allModels) {
        if (model.getUri().toString().contains(targetProblemSchemeURIPart)) {
          data.addAll(getPerfDataForScheme(model));
        }
      }
    } catch (Exception e) {
      SimSystem.report(Level.SEVERE,
          "Processing performance data from database failed.", e);
    }

    return new PerformanceDataSet<>(data,
        targetPerfMeasurerFactory.isForMaximisation());
  }

  /**
   * Creates a list of performance tuples for a given problem scheme.
   * 
   * @param scheme
   *          the problem scheme
   * @return list of all performance tuples associated with this model
   */
  protected List<ProblemPerformanceTuple> getPerfDataForScheme(
      IProblemScheme scheme) {
    List<ProblemPerformanceTuple> perfTuples = new ArrayList<>();
    Map<IProblemDefinition, List<IRuntimeConfiguration>> simProblemConfigs =
        new HashMap<>();
    int maxRTCNum = 0;
    Map<Long, Configuration> rtcToConfig = new HashMap<>();

    // Look for the largest configuration coverage (i.e., amount of
    // configurations tried for a given problem)
    List<IProblemDefinition> problemDefinitions =
        perfDB.getAllProblemDefinitions(scheme);
    for (IProblemDefinition problemDefinition : problemDefinitions) {
      List<IRuntimeConfiguration> rtConfigs =
          perfDB.getAllRuntimeConfigs(problemDefinition);
      filterBlackList(rtConfigs);
      simProblemConfigs.put(problemDefinition, rtConfigs);
      maxRTCNum = Math.max(maxRTCNum, rtConfigs.size());
      for (IRuntimeConfiguration rtConfig : rtConfigs) {
        if (!rtcToConfig.containsKey(rtConfig.getID())) {
          rtcToConfig.put(rtConfig.getID(), createConfiguration(rtConfig));
        }
      }
    }

    int probCounter = 0;
    int overallProblems = problemDefinitions.size();
    int minimalRTConfigCount = (int) (maxRTCNum * rtConfigCoverageFactor);

    final Map<Long, Double> perfData =
        perfDB.getAllPerformancesMap(targetPerfType);

    for (Entry<IProblemDefinition, List<IRuntimeConfiguration>> entry : simProblemConfigs
        .entrySet()) {
      IProblemDefinition problem = entry.getKey();
      List<IRuntimeConfiguration> rtConfigs = entry.getValue();
      SimSystem.report(Level.INFO, "Analyzing problem #" + (++probCounter)
          + "/" + overallProblems + " ("
          + ((probCounter * 100.0) / overallProblems) + "%)");
      if (minimalRTConfigCount > rtConfigs.size()) {
        SimSystem.report(Level.INFO, "Too few configs (" + rtConfigs.size()
            + "), problem will be ignored.");
        continue;
      }
      perfTuples.addAll(generateTuplesForProblem(problem, rtConfigs,
          rtcToConfig, perfData));
      perfDB.flush();
    }

    return perfTuples;
  }

  /**
   * Filters configurations according to black list.
   * 
   * @param rtConfigs
   *          the list of runtime configurations to be filtered
   */
  private void filterBlackList(List<IRuntimeConfiguration> rtConfigs) {

    if (blackList.size() == 0) {
      return;
    }

    List<IRuntimeConfiguration> removeConfigs = new ArrayList<>();
    for (IRuntimeConfiguration rtConfig : rtConfigs) {
      for (Class<?> facClass : rtConfig.getSelectionTree().getUniqueFactories()) {
        for (String forbidden : blackList) {
          if (facClass.getName().contains(forbidden)) {
            removeConfigs.add(rtConfig);
            break;
          }
        }
      }
    }

    for (IRuntimeConfiguration removeConfig : removeConfigs) {
      rtConfigs.remove(removeConfig);
    }
  }

  /**
   * Generates all {@link ProblemPerformanceTuple} instances for a given
   * {@link IProblemDefinition}.
   * 
   * @param problem
   *          the given simulation problem
   * @param rtConfigs
   *          list of runtime configurations that have been applied to this
   *          problem
   * @param rtcToConfig
   * @param perfData
   * @param perfData
   * @return list of generated performance tuples
   */
  private List<ProblemPerformanceTuple> generateTuplesForProblem(
      IProblemDefinition problem, List<IRuntimeConfiguration> rtConfigs,
      Map<Long, Configuration> rtcToConfig, Map<Long, Double> perfData) {

    List<ProblemPerformanceTuple> perfTuples = new ArrayList<>();

    for (IRuntimeConfiguration rtConfig : rtConfigs) {
      try {
        Configuration config = rtcToConfig.get(rtConfig.getID());
        List<Pair<Features, Double>> results =
            extractFeaturePerformanceTuples(problem, rtConfig, perfData);

        // Check whether features are same for all applications - only
        // create *one* performance tuple if that is the case
        if (allFeaturesEqual(results)) {
          perfTuples.add(new ProblemPerformanceTuple(problem, results.get(0)
              .getFirstValue(), config, targetPerfType
              .getPerformanceMeasurerFactory(), sumPerformance(results)
              / results.size()));
        } else {
          for (Pair<Features, Double> perfFeaturesTuple : results) {
            perfTuples.add(new ProblemPerformanceTuple(problem,
                perfFeaturesTuple.getFirstValue(), config, targetPerfType
                    .getPerformanceMeasurerFactory(), perfFeaturesTuple
                    .getSecondValue()));
          }
        }
      } catch (Throwable t) { // NOSONAR: This should be as robust as possible
        SimSystem.report(Level.SEVERE,
            "Generation of performance tuples failed.", t);
      }
    }

    return perfTuples;
  }

  /**
   * Extracts a tuple (features, performance) for all applications of problem
   * and runtime configuration.
   * 
   * @param problem
   *          the problem
   * @param perfData
   * @param perfData
   * @param RUNTIME_CONFIGURATION
   *          the runtime configuration
   * 
   * @return a list of (features,performance) tuples
   * 
   * @throws Exception
   *           if DB performance/feature extraction fails
   */
  private List<Pair<Features, Double>> extractFeaturePerformanceTuples(
      IProblemDefinition problem, IRuntimeConfiguration rtConfig,
      Map<Long, Double> perfData) {
    List<Pair<Features, Double>> results = new ArrayList<>();
    for (IApplication application : perfDB
        .getAllApplications(problem, rtConfig)) {
      Double performance = perfData.get(application.getID());
      if (performance == null) {
        continue;
      }
      // TODO distinguish all three levels: problem - problem instance -
      // application, aggregate on every one
      results.add(new Pair<>(createOrReadFeatures(application), performance));
    }
    return results;
  }

  /**
   * Checks whether all elements in list are equal.
   * 
   * @param perfFeaturesTuples
   *          the list of problem features
   * 
   * @return true, if successful
   */
  private boolean allFeaturesEqual(
      List<Pair<Features, Double>> perfFeaturesTuples) {
    for (int i = 1; i < perfFeaturesTuples.size(); i++) {
      if (!perfFeaturesTuples.get(i - 1).getFirstValue()
          .equals(perfFeaturesTuples.get(i).getFirstValue())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sums up performance of all results (second tuple element).
   * 
   * @param perfFeaturesTuples
   *          the tuples (features, performance)
   * 
   * @return the sum of all performances
   */
  private Double sumPerformance(List<Pair<Features, Double>> perfFeaturesTuples) {
    double perfSum = 0.;
    for (Pair<Features, Double> perfFeaturesTuple : perfFeaturesTuples) {
      perfSum += perfFeaturesTuple.getSecondValue();
    }
    return perfSum;
  }

  /**
   * Flattens the
   * {@link org.jamesii.perfdb.recording.selectiontrees.SelectionTree}
   * associated with the {@link IRuntimeConfiguration} to a
   * {@link Configuration} for data mining purposes.
   * 
   * @param config
   *          the runtime configuration at hand
   * @return the corresponding configuration map for the data mining components
   */
  public static Configuration createConfiguration(IRuntimeConfiguration config) {
    return new Configuration(config.getSelectionTree());
  }

  /**
   * Extracts features for a given {@link IProblemDefinition} and stores them to
   * {@link Features} (if {@link #applyingFeatureExtraction} is true).
   * 
   * @param application
   *          the application at hand
   * @return the {@link Features} describing the simulation problem
   * @throws Exception
   *           if database look-up fails
   */
  protected Features createOrReadFeatures(IApplication application) {
    if (isApplyingFeatureExtraction()) {
      createFeatures(application);
    }
    Features features = new Features();
    for (IFeature feature : perfDB.getAllFeatures(application)) {
      features.putAll(feature.getValue());
    }
    return features;
  }

  /**
   * Creates features for the given application (if missing).
   * 
   * @param application
   *          the application
   */
  private void createFeatures(IApplication application) {
    if (extractorFactories == null) {
      extractorFactories =
          FeatureExtraction.getFeatureExtractorsForApplication(application);
    }
    FeatureExtraction.extractFeatures(application, perfDB, featureMap,
        extractorFactories);
  }

  public boolean isApplyingFeatureExtraction() {
    return applyingFeatureExtraction;
  }

  public void setApplyingFeatureExtraction(boolean applyingFeatureExtraction) {
    this.applyingFeatureExtraction = applyingFeatureExtraction;
  }
}
