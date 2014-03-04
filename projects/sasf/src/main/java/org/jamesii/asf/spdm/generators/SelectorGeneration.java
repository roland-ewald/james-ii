/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

import java.util.List;
import java.util.Set;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.util.PredictorGenerators;


/**
 * Creates {@link ISelector} instances from generated
 * {@link IPerformancePredictor} instances.
 * 
 * @author Roland Ewald
 */
public final class SelectorGeneration {

  /** The default result size of the selector to be created. */
  public static final int DEFAULT_RESULT_SIZE = 10;

  /**
   * Should not be instantiated, helper class.
   */
  private SelectorGeneration() {
  }

  /**
   * Creates a selector.
   * 
   * @param performanceDataSet
   *          the performance data set
   * @param predictor
   *          the predictor
   * @return the selector
   */
  public static ISelector createSelector(PerformanceDataSet performanceDataSet,
      IPerformancePredictor predictor) {
    return createSelector(performanceDataSet.getInstances(),
        performanceDataSet.getMetaData(), predictor);
  }

  /**
   * Creates a selector.
   * 
   * @param configurations
   *          the configurations
   * @param metaData
   *          the meta performance data
   * @param predictor
   *          the predictor
   * @return the selector
   */
  public static ISelector createSelector(Set<Configuration> configurations,
      PerfTupleMetaData metaData, IPerformancePredictor predictor) {
    return new Selector(configurations, predictor, DEFAULT_RESULT_SIZE,
        metaData.isMaximizePerformance());
  }

  /**
   * Creates a selector.
   * 
   * @param trainingData
   *          the training data
   * @param metaData
   *          the meta performance data
   * @param predictor
   *          the predictor
   * @return the selector
   */
  public static ISelector createSelector(List<PerformanceTuple> trainingData,
      PerfTupleMetaData metaData, IPerformancePredictor predictor) {
    return createSelector(PredictorGenerators.getDistinctConfigs(trainingData),
        metaData, predictor, DEFAULT_RESULT_SIZE);
  }

  /**
   * Creates a selector.
   * 
   * @param trainingData
   *          the training data
   * @param metaData
   *          the meta performance data
   * @param predictor
   *          the predictor
   * @param maxResultSize
   *          the maximal size of the selection result list
   * @return the selector
   */
  public static ISelector createSelector(List<PerformanceTuple> trainingData,
      PerfTupleMetaData metaData, IPerformancePredictor predictor,
      int maxResultSize) {
    return createSelector(PredictorGenerators.getDistinctConfigs(trainingData),
        metaData, predictor, maxResultSize);
  }

  /**
   * Creates a selector.
   * 
   * @param configurations
   *          the configurations
   * @param metaData
   *          the meta performance data
   * @param predictor
   *          the predictor
   * @param maxResultSize
   *          the maximal size of the selection result list
   * @return the selector
   */
  public static ISelector createSelector(Set<Configuration> configurations,
      PerfTupleMetaData metaData, IPerformancePredictor predictor,
      int maxResultSize) {
    return new Selector(configurations, predictor, maxResultSize,
        metaData.isMaximizePerformance());
  }

}
