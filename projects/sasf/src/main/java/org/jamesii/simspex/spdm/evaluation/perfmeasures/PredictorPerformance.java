/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.perfmeasures;


import java.util.Map;

import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.spdm.evaluation.evaluator.IPredictorEvaluator;


/**
 * Represents the performance metrics of a predictor.
 * 
 * @author Roland Ewald
 * 
 */
public class PredictorPerformance {

  /**
   * Theoretical error measurements. Map from performance measure to tuple
   * (error sum, #trials).
   */
  private final Map<Class<? extends IPredictorPerformanceMeasure>, Pair<Double, Integer>> performanceMeasures;

  /** Size of the training set. */
  private final int trainingSetSize;

  /** Size of test set. */
  private final int testSetSize;

  /** Evaluation method. */
  private final Class<? extends IPredictorEvaluator> predictorEvaluator;

  /**
   * Default constructor.
   * 
   * @param trainSize
   *          size of training set
   * @param testSize
   *          size of test set
   * @param evaluator
   *          evaluation method
   * @param perfMeasures
   *          mapping feature class -> actual feature (expressed as a double
   *          value for the error and an integer value for the number of trials)
   */
  public PredictorPerformance(
      int trainSize,
      int testSize,
      Class<? extends IPredictorEvaluator> evaluator,
      Map<Class<? extends IPredictorPerformanceMeasure>, Pair<Double, Integer>> perfMeasures) {
    trainingSetSize = trainSize;
    testSetSize = testSize;
    predictorEvaluator = evaluator;
    performanceMeasures = perfMeasures;
  }

  public int getTrainingSetSize() {
    return trainingSetSize;
  }

  public int getTestSetSize() {
    return testSetSize;
  }

  public Class<? extends IPredictorEvaluator> getPredictorEvaluator() {
    return predictorEvaluator;
  }

  public Map<Class<? extends IPredictorPerformanceMeasure>, Pair<Double, Integer>> getPerformanceMeasures() {
    return performanceMeasures;
  }

}
