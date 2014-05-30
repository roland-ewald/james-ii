/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording;


import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.features.plugintype.AbstractFeatureExtractorFactory;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;
import org.jamesii.perfdb.recording.features.plugintype.IFeatureExtractor;
import org.jamesii.perfdb.recording.performance.IPerformanceMeasurer;
import org.jamesii.perfdb.recording.performance.plugintype.AbstractPerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;
import org.jamesii.simspex.gui.ProblemInstanceGenerator;
import org.jamesii.simspex.util.BenchmarkModelType;
import org.jamesii.simspex.util.DatabaseUtils;
import org.jamesii.simspex.util.SimulationProblemDefinition;


/**
 * Class to help writing performance data to the performance database.
 * 
 * @param IPerformanceDatabase
 * 
 * @author Roland Ewald
 * 
 */
public class PerfDBRecordingHelper {

  /** The generator for problem instances (deals with RNG setup). */
  private ProblemInstanceGenerator instanceGenerator = new ProblemInstanceGenerator();

  /** The execution counter. */
  private int executionCounter;

  /** The desired flush interval. */
  private int flushInterval = 1;

  /** The performance database to be used. */
  private final IPerformanceDatabase database;

  /** The performance measurers that already have been used. */
  private final Map<String, IPerformanceMeasurer<?>> performanceMeasurers = new HashMap<>();

  /**
   * Instantiates a new performance database recording helper.
   * 
   * @param performanceDatabase
   *          the performance database to which shall be recorded
   */
  public PerfDBRecordingHelper(IPerformanceDatabase performanceDatabase) {
    database = performanceDatabase;
  }

  /**
   * Store problem instance to database (if non-existent).
   * 
   * @param problemSchemeURI
   *          the problem scheme URI
   * @param parameters
   *          the parameters for the problem scheme
   * @param stopFactoryClass
   *          the stop factory class
   * @param stopFactoryParameters
   *          the stop factory parameters
   * @return the problem instance entity stored in the database
   */
  public IProblemInstance storeProblemInstance(URI problemSchemeURI,
      Map<String, Serializable> parameters,
      Class<? extends ComputationTaskStopPolicyFactory> stopFactoryClass,
      ParameterBlock stopFactoryParameters) {

    IProblemScheme scheme = database.getProblemScheme(problemSchemeURI);

    if (scheme == null) {
      scheme = setupProblemScheme(problemSchemeURI);
    }

    IProblemDefinition problemDefinition = null;

    // Create new simulation problem or re-use existing one
    problemDefinition = database.newProblemDefinition(scheme,
        SimulationProblemDefinition.getDefinitionParameters(stopFactoryClass,
            stopFactoryParameters),
        parameters == null ? new HashMap<String, Serializable>() : parameters);

    // Generate/Re-use problem instance
    Pair<Long, String> rngSetup = instanceGenerator
        .requestNewInstance(problemDefinition);
    IProblemInstance problemInstance = database.newProblemInstance(
        problemDefinition, rngSetup != null ? rngSetup.getFirstValue() : -1,
        rngSetup != null ? rngSetup.getSecondValue() : null);

    flushIfNecessary();
    return problemInstance;
  }

  /**
   * Stores problem instance.
   * 
   * @param compTaskConfig
   *          the computation task configuration
   * @return the problem instance
   */
  @SuppressWarnings("unchecked")
  // The parameters stored in the map of the computation task config *need* to
  // be serializable, so that they can be stored
  public IProblemInstance storeProblemInstance(
      IComputationTaskConfiguration compTaskConfig) {
    URI uri = compTaskConfig.getAbsModelReaderFactoryParams().getSubBlockValue(
        IURIHandling.URI);
    return storeProblemInstance(uri,
        (Map<String, Serializable>) compTaskConfig.getParameters(),
        compTaskConfig.getStopPolicyFactoryClass(),
        compTaskConfig.getStopPolicyParameters());
  }

  /**
   * Setup a problem scheme.
   * 
   * @param uri
   *          the uri
   * @return the problem scheme
   */
  private IProblemScheme setupProblemScheme(URI uri) {
    // TODO add ways to manipulate these values, or to install a callback (e.g.
    // a dialog to input BM model name and description=
    return database.newProblemScheme(uri, "Model name", DatabaseUtils
        .convertModelTypeToSchemeType(BenchmarkModelType.APPLICATION), "-");
  }

  /**
   * Flush performance database if necessary.
   * 
   * @throws Exception
   *           the exception
   */
  private void flushIfNecessary() {
    executionCounter++;
    if (executionCounter % flushInterval == 0) {
      database.flush();
    }
  }

  /**
   * Store configuration for problem.
   * 
   * @param selectionTree
   *          the selection tree
   * @param problemInstance
   *          the problem instance
   * @return the runtime configuration
   */
  public IRuntimeConfiguration storeConfigurationForProblem(
      SelectionTree selectionTree, IProblemInstance problemInstance) {
    IRuntimeConfiguration rtConfig = registerRuntimeConfig(selectionTree);
    flushIfNecessary();
    return rtConfig;
  }

  /**
   * Register a runtime configuration.
   * 
   * @param selectionTree
   *          the selection tree
   * @return the runtime configuration
   */
  private IRuntimeConfiguration registerRuntimeConfig(
      SelectionTree selectionTree) {
    return database.newRuntimeConfiguration(selectionTree, false);
  }

  /**
   * Stores the application of a runtime configuration to a problem instance.
   * 
   * @param probInst
   *          the problem instance
   * @param rtConfig
   *          the runtime configuration
   * @return the application entity from the database
   */
  public IApplication storeApplication(IProblemInstance probInst,
      IRuntimeConfiguration rtConfig) {
    // TODO create j2 ds result provider (ask Registry for factory etc.)
    // TODO create representation of hardware setup
    IApplication application = database.newApplication(probInst, rtConfig,
        null, null);
    flushIfNecessary();
    return application;
  }

  /**
   * Store performance to database.
   * 
   * @param <T>
   *          the type of data analyzed to measure the performance
   * @param application
   *          the application
   * @param performanceData
   *          the performance data
   * @return the map performance measurer factory => measured performance
   */
  public <T extends Serializable> Map<PerformanceMeasurerFactory, Double> storePerformance(
      IApplication application, T performanceData,
      Map<String, ?> problemParameters, ParameterBlock performanceMeasurerParams) {

    // Select suitable performance measurers
    Map<Class<? extends PerformanceMeasurerFactory>, IPerformanceType> factoryMapping = new HashMap<>();
    for (IPerformanceType perfMeasure : database.getAllPerformanceTypes()) {
      factoryMapping.put(perfMeasure.getPerformanceMeasurerFactory(),
          perfMeasure);
    }

    ParameterBlock perfMeasurerParams = performanceMeasurerParams.getCopy();
    perfMeasurerParams.addSubBl(PerformanceMeasurerFactory.PERFORMANCE_DATA,
        performanceData);
    perfMeasurerParams.addSubBl(PerformanceMeasurerFactory.PROBLEM_PARAMETERS,
        problemParameters);

    Map<PerformanceMeasurerFactory, Double> performances = applySuitablePerformanceMeasures(
        performanceData, application, factoryMapping, perfMeasurerParams);

    flushIfNecessary();
    return performances;
  }

  /**
   * Applies all suitable performance measures.
   * 
   * @param <T>
   *          the generic type
   * @param performanceData
   *          the performance data
   * @param application
   *          the application
   * @param factoryMapping
   *          the factory mapping
   * @param perfMeasurerParams
   *          the performance measurer parameters
   */
  private <T> Map<PerformanceMeasurerFactory, Double> applySuitablePerformanceMeasures(
      T performanceData,
      IApplication application,
      Map<Class<? extends PerformanceMeasurerFactory>, IPerformanceType> factoryMapping,
      ParameterBlock perfMeasurerParams) {

    List<PerformanceMeasurerFactory> suitableFactories = SimSystem
        .getRegistry().getFactoryOrEmptyList(
            AbstractPerformanceMeasurerFactory.class, perfMeasurerParams);

    // Measure performance, and store it to database
    Map<PerformanceMeasurerFactory, Double> performances = new HashMap<>();
    for (PerformanceMeasurerFactory suitableFactory : suitableFactories) {
      performances.put(
          suitableFactory,
          registerPerformance(performanceData, application,
              factoryMapping.get(suitableFactory.getClass()), suitableFactory,
              perfMeasurerParams));
    }

    return performances;
  }

  /**
   * Register performance.
   * 
   * @param <T>
   *          the type of the data on which performance measurement is based
   * @param performanceData
   *          the performance data
   * @param application
   *          the application
   * @param performanceType
   *          the performance type
   * @param perfMeasurerFactory
   *          the factory to create the performance measurer
   * @param perfMeasurerParams
   *          the parameters for the performance measurer
   * @return the performance that has been stored
   */
  @SuppressWarnings("unchecked")
  // Conformance to IPerformanceMeasurer<T> should be guaranteed by the
  // factories, checking for the runtime
  // configuration in the parameter block
  private <T> double registerPerformance(T performanceData,
      IApplication application, IPerformanceType performanceType,
      PerformanceMeasurerFactory perfMeasurerFactory,
      ParameterBlock perfMeasurerParams) {

    String perfMeasurerHash = PerformanceMeasurerFactory
        .getPerformanceMeasurerHash(perfMeasurerFactory, perfMeasurerParams);
    IPerformanceMeasurer<T> perfMeasurer = (IPerformanceMeasurer<T>) performanceMeasurers
        .get(perfMeasurerHash);
    if (perfMeasurer == null) {
      perfMeasurer = (IPerformanceMeasurer<T>) perfMeasurerFactory
          .create(perfMeasurerParams);
      performanceMeasurers.put(perfMeasurerHash, perfMeasurer);
    }
    Double performance = perfMeasurer.measurePerformance(performanceData);

    if (performance.isInfinite() || performance.isNaN()) {
      performance = -Double.MAX_VALUE;
      SimSystem.report(
          Level.WARNING,
          "Could not store valid performance with '"
              + perfMeasurerFactory.getName() + "', setting to -max_value.");
    }

    database.newPerformance(application, performanceType, performance);
    return performance;
  }

  /**
   * Store features.
   * 
   * @param application
   *          the application with which the features shall be associated
   * @param problemRepresentation
   *          the problem representation
   */
  public <P> void storeFeatures(IApplication application,
      P problemRepresentation) {

    ParameterBlock parameters = new ParameterBlock(problemRepresentation,
        FeatureExtractorFactory.PROBLEM_REPRESENTATION);

    List<FeatureExtractorFactory> suitableFactories = SimSystem.getRegistry()
        .getFactoryOrEmptyList(AbstractFeatureExtractorFactory.class,
            parameters);

    // Select suitable feature extractors
    Map<Class<? extends FeatureExtractorFactory>, IFeatureType> factoryMapping = new HashMap<>();
    for (IFeatureType featureType : database.getAllFeatureTypes()) {
      factoryMapping.put(featureType.getFeatureExtractorFactory(), featureType);
    }

    Map<String, Serializable> allFeatures = new HashMap<>();
    for (FeatureExtractorFactory suitableFactory : suitableFactories) {
      IFeatureType featureType = factoryMapping.get(suitableFactory.getClass());

      @SuppressWarnings("unchecked")
      // Should be guaranteed by the factories, checking for problem
      // representation in the parameter block
      Map<String, Serializable> extractedFeatures = ((IFeatureExtractor<P>) suitableFactory
          .create(parameters)).extractFeatures(problemRepresentation);
      allFeatures.putAll(extractedFeatures);
      database.newFeature(application, featureType, extractedFeatures);
    }

  }

  /**
   * Gets the flush interval.
   * 
   * @return the flush interval
   */
  public int getFlushInterval() {
    return flushInterval;
  }

  /**
   * Sets the flush interval.
   * 
   * @param flushInterval
   *          the new flush interval
   */
  public void setFlushInterval(int flushInterval) {
    this.flushInterval = flushInterval;
  }

}
