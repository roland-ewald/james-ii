/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.ISelector;
import org.jamesii.asf.spdm.generators.SelectorGeneration;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.asf.spdm.generators.preprocess.IDMDataPreProcessor;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.recording.features.FeatureExtraction;
import org.jamesii.simspex.spdm.dataimport.database.DBImportManagerFactory;
import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.IPredictorPerformanceMeasure;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.PredictorPerformance;
import org.jamesii.simspex.spdm.util.PredictorPerformanceAnalysis;

/**
 * This class subsumes all operations necessary to generate and evaluate
 * selectors. This includes feature extraction.
 * 
 * @author Roland Ewald
 */
public class SelectorGenerationTask {

  /** The benchmark model from which to use performance data. */
  public final String model;

  /** The numbers of replicating the generation and evaluation of a selector. */
  public final int replications;

  /** The selector performance measure to consider. */
  public final Class<? extends IPredictorPerformanceMeasure> selectorPerformanceMeasure;

  /** The factories for the selector generators to be evaluated. */
  public final PerformancePredictorGeneratorFactory[] selectorGeneratorFactories;

  /** Contains additional parameters for the selector generator factories. */
  final protected Map<PerformancePredictorGeneratorFactory, ParameterBlock> selectorGenerationFactoryParameters;

  /** The evaluation strategy to be used for selector generators. */
  public final IPredictorGeneratorEvaluationStrategy evaluationStrategy;

  /** List of all kinds of features that have been extracted. */
  List<IFeatureType> featureTypes;

  /** Selector performances. Ordered by performance. */
  List<Pair<PerformancePredictorGeneratorFactory, List<PredictorPerformance>>> selPerformances;

  /** The set of performance data considered for selector generation. */
  PerformanceDataSet<PerformanceTuple> perfDataSet;

  /** Generated selectors. Ordered by performance. */
  List<ISelector> selectors;

  /**
   * Instantiates a new selector generation task.
   * 
   * @param benchmarkModel
   *          the benchmark model
   * @param numOfReplications
   *          the number of replications
   * @param selPerfMeasure
   *          the selector performance measure
   * @param selGenFactories
   *          the selector generation factories
   * @param selGenFactoryParamMap
   *          the parameters fot the selector generation factories
   * @param selectorEvaluationStrategy
   *          the selector evaluation strategy
   */
  public SelectorGenerationTask(
      String benchmarkModel,
      int numOfReplications,
      Class<? extends IPredictorPerformanceMeasure> selPerfMeasure,
      PerformancePredictorGeneratorFactory[] selGenFactories,
      Map<PerformancePredictorGeneratorFactory, ParameterBlock> selGenFactoryParamMap,
      IPredictorGeneratorEvaluationStrategy selectorEvaluationStrategy) {
    model = benchmarkModel;
    replications = numOfReplications;
    selectorPerformanceMeasure = selPerfMeasure;
    selectorGeneratorFactories = selGenFactories;
    selectorGenerationFactoryParameters = selGenFactoryParamMap;
    evaluationStrategy = selectorEvaluationStrategy;
  }

  /**
   * Extract features.
   * 
   * @param perfDB
   *          the perf db
   * 
   * @throws Exception
   *           the exception
   */
  public void extractFeatures(IPerformanceDatabase perfDB) throws Exception {
    FeatureExtraction.refreshFeatureExtractors(perfDB);
    FeatureExtraction featureExtraction = new FeatureExtraction(perfDB);
    featureExtraction.extractFeatures();
    featureTypes = perfDB.getAllFeatureTypes();
  }

  /**
   * Gets the feature types.
   * 
   * @return the feature types
   */
  public List<IFeatureType> getFeatureTypes() {
    return featureTypes;
  }

  /**
   * Execute selector generation and evaluation.
   * 
   * @param perfDB
   *          the perf db
   * @param exploration
   *          the exploration
   * 
   * @throws Exception
   *           the exception
   */

  @SuppressWarnings("unchecked")
  public void executeSelectorGenerationAndEvaluation(
      IPerformanceDatabase perfDB, PerformanceExplorationTask exploration)
      throws Exception {
    @SuppressWarnings("rawtypes")
    IDMDataImportManager dbImportManager =
        (new DBImportManagerFactory()).create((new ParameterBlock(perfDB,
            DBImportManagerFactory.PERFORMANCE_DATABASE).addSubBl(
            DBImportManagerFactory.TARGET_MODEL, model)
            .addSubBl(DBImportManagerFactory.TARGET_PERF_MEASURE,
                exploration.perfMeasure)), SimSystem.getRegistry().createContext());

    perfDataSet = dbImportManager.getPerformanceData();
    selPerformances = evaluateSelectorGenerators();

    selectors = new ArrayList<>();
    for (Pair<PerformancePredictorGeneratorFactory, List<PredictorPerformance>> selPerformance : selPerformances) {
      selectors.add(generateSelector(selPerformance.getFirstValue()));
    }

    selectors = Collections.unmodifiableList(selectors);
  }

  /**
   * Evaluate selector generators.
   * 
   * @return the list< pair< selector generator factory, list< selector
   *         performance>>>
   * 
   * @throws Exception
   *           the exception
   */
  protected List<Pair<PerformancePredictorGeneratorFactory, List<PredictorPerformance>>> evaluateSelectorGenerators()
      throws Exception {
    selPerformances = new ArrayList<>();
    for (PerformancePredictorGeneratorFactory factory : selectorGeneratorFactories) {
      ParameterBlock parameters = new ParameterBlock();
      if (selectorGenerationFactoryParameters != null
          && selectorGenerationFactoryParameters.containsKey(factory)) {
        parameters = selectorGenerationFactoryParameters.get(factory);
      }
      selPerformances.add(new Pair<>(factory, evaluationStrategy
          .evaluatePredictorGenerator(factory, perfDataSet, parameters)));
    }
    Collections
        .sort(
            selPerformances,
            new Comparator<Pair<PerformancePredictorGeneratorFactory, List<PredictorPerformance>>>() {
              @Override
              public int compare(
                  Pair<PerformancePredictorGeneratorFactory, List<PredictorPerformance>> o1,
                  Pair<PerformancePredictorGeneratorFactory, List<PredictorPerformance>> o2) {
                Double avgError1 = calculateAverageError(o1.getSecondValue());
                Double avgError2 = calculateAverageError(o2.getSecondValue());
                return avgError1.compareTo(avgError2);
              }

            });
    return Collections.unmodifiableList(selPerformances);
  }

  /**
   * Generate selector.
   * 
   * @param factory
   *          the factory
   * 
   * @return the i selector
   * 
   * @throws Exception
   *           the exception
   */
  protected ISelector generateSelector(
      PerformancePredictorGeneratorFactory factory) throws Exception {
    // Use pre-processor if available
    IDMDataPreProcessor<PerformanceTuple> preProcessor =
        factory.createPreprocessor(new ParameterBlock());
    if (preProcessor != null) {
      perfDataSet.setInstances(preProcessor.preprocess(perfDataSet
          .getInstances()));
    }
    if (perfDataSet.getInstances().size() == 0) {
      throw new RuntimeException("Performance data set is empty!");
    }
    ParameterBlock parameters = new ParameterBlock();
    if (selectorGenerationFactoryParameters != null
        && selectorGenerationFactoryParameters.containsKey(factory)) {
      parameters = selectorGenerationFactoryParameters.get(factory);
    }
    IPerformancePredictorGenerator sg =
        factory.createPredictorGenerator(parameters, perfDataSet.getInstances()
            .get(0));

    return SelectorGeneration.createSelector(
        perfDataSet.getInstances(),
        perfDataSet.getMetaData(),
        sg.generatePredictor(perfDataSet.getInstances(),
            perfDataSet.getMetaData()));
  }

  /**
   * Calculate average error.
   * 
   * @param performances
   *          the performances
   * 
   * @return the double
   */
  public Double calculateAverageError(List<PredictorPerformance> performances) {
    Map<Class<? extends IPredictorPerformanceMeasure>, Pair<Double, Integer>> avgPerformances =
        PredictorPerformanceAnalysis.getAverages(performances);
    if (replications != performances.size()) {
      throw new RuntimeException();
    }
    if (avgPerformances.size() == 0) {
      throw new RuntimeException("List of performances is empty!");
    }

    Pair<Double, Integer> avgPerformance =
        avgPerformances.get(this.selectorPerformanceMeasure);
    return avgPerformance.getFirstValue() / avgPerformance.getSecondValue();
  }

  /**
   * Gets the perf data set.
   * 
   * @return the perf data set
   */
  public PerformanceDataSet<PerformanceTuple> getPerfDataSet() {
    return perfDataSet;
  }

  /**
   * Gets the selectors.
   * 
   * @return the selectors
   */
  public List<ISelector> getSelectors() {
    return selectors;
  }

  /**
   * Gets the selector performances.
   * 
   * @return the selector performances
   */
  public List<Pair<PerformancePredictorGeneratorFactory, List<PredictorPerformance>>> getSelectorPerformances() {
    return selPerformances;
  }

}
