/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.evaluator;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.IPredictorPerformanceMeasure;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.PredictorPerformance;


/**
 * Super class for predictor evaluators. A predictor evaluator evaluates a
 * <i>single</i> predictor, not to be confused with predictor generator
 * evaluators, which evaluate the quality of a <i>generator</i> for a given
 * problem set.
 * 
 * @see IPerformancePredictor
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PredictorEvaluator implements IPredictorEvaluator {

  @Override
  public <T extends PerformanceTuple> PredictorPerformance evaluate(
      IPerformancePredictor predictor, List<T> trainingData, List<T> testData,
      ParameterBlock parameters) {

    Map<Class<? extends IPredictorPerformanceMeasure>, Pair<Double, Integer>> performances =
        new HashMap<>();
    List<IPredictorPerformanceMeasure> selPerfMeasures =
        getPredictorPerfMeasures(parameters);

    int trainSize = trainingData.size();
    int testSize = testData.size();

    for (IPredictorPerformanceMeasure selPerfMeasure : selPerfMeasures) {
      performances.put(selPerfMeasure.getClass(),
          selPerfMeasure.calculatePerformance(testData, predictor));
    }

    Class<? extends IPredictorEvaluator> evaluator = getEvalClass();

    return new PredictorPerformance(trainSize, testSize, evaluator,
        performances);
  }

  /**
   * Get predictor performance measures that correspond to this evaluator.
   * 
   * @param parameters
   *          parameters for performance measures
   * @return list of predictor performance measures
   */
  public abstract List<IPredictorPerformanceMeasure> getPredictorPerfMeasures(
      ParameterBlock parameters);

  /**
   * Get current class for storage in {@link PredictorPerformance}.
   * 
   * @return current class
   */
  public abstract Class<? extends IPredictorEvaluator> getEvalClass();

}
