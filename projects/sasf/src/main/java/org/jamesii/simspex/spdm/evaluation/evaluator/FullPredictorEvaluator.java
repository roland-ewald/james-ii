/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.evaluator;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.IPredictorPerformanceMeasure;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.plugintype.PredictorPerfMeasureFactory;


/**
 * Evaluator that uses all available predictor performance measurements.
 * 
 * @author Roland Ewald
 */
public class FullPredictorEvaluator extends PredictorEvaluator {

  @Override
  public List<IPredictorPerformanceMeasure> getPredictorPerfMeasures(
      ParameterBlock parameters) {

    List<IPredictorPerformanceMeasure> results =
        new ArrayList<>();
    List<PredictorPerfMeasureFactory> factories =
        SimSystem.getRegistry().getFactories(PredictorPerfMeasureFactory.class);

    for (PredictorPerfMeasureFactory factory : factories) {
      results.add(factory.create(parameters, SimSystem.getRegistry().createContext()));
    }

    return results;
  }

  @Override
  public Class<? extends IPredictorEvaluator> getEvalClass() {
    return this.getClass();
  }

}
