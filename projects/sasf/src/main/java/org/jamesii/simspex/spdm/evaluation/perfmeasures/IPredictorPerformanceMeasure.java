/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.perfmeasures;


import java.util.List;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.core.util.misc.Pair;


/**
 * Interface for error calculation methods. They take the test data and the
 * predictor.
 * 
 * @author Roland Ewald
 * 
 */
public interface IPredictorPerformanceMeasure {

  /**
   * Calculates performance for a given test set and a predictor.
   * 
   * @param testData
   *          list of test tuples
   * @param predictor
   *          the predictor to be evaluated
   * @return performance measurement, a pair (absolute error, number of
   *         predictions)
   */
  <T extends PerformanceTuple> Pair<Double, Integer> calculatePerformance(
      List<T> testData, IPerformancePredictor predictor);

}
