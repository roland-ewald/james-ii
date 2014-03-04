/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.evaluator;


import java.util.List;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.PredictorPerformance;


/**
 * Interface for predictor evaluation components. Not to be confused with
 * predictor generator evaluation, e.g. bootstrapping or cross-validation.
 * 
 * @see IPerformancePredictor
 * 
 * @author Roland Ewald
 * 
 */
public interface IPredictorEvaluator {

  /**
   * Evaluates a predictor with test and training data as predefined by the
   * corresponding data selectors.
   * 
   * @param <T>
   *          the type of the performance tuples
   * @param predictor
   *          the predictor to be evaluated
   * @param trainingData
   *          the training data used to generate this predictor
   * @param testData
   *          the test data that is available for this method
   * @param parameters
   *          parameters for the predictor evaluator and the performance
   *          measurements
   * @return predictor performance measurements
   */
  <T extends PerformanceTuple> PredictorPerformance evaluate(
      IPerformancePredictor predictor, List<T> trainingData, List<T> testData,
      ParameterBlock parameters);

}
