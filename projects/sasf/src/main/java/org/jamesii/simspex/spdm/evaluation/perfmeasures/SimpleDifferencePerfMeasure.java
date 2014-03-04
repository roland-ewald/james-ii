/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.perfmeasures;

import java.util.List;

import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.ConfigurationEntry;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.core.util.misc.Pair;

/**
 * Simple error measure that adds up the distance of the predicted performances.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleDifferencePerfMeasure extends SimpleBooleanPerfMeasure {

  @Override
  protected <T extends PerformanceTuple> Pair<Double, Integer> calculateError(
      Features features, List<T> testTuples, IPerformancePredictor predictor) {
    double errors = 0;
    for (PerformanceTuple testTuple : testTuples) {
      errors +=
          comparePrediction(predictor, features, new ConfigurationEntry(
              testTuple.getConfiguration()), testTuple);
    }

    return new Pair<>(errors, testTuples.size());
  }

  private double comparePrediction(IPerformancePredictor perfPred,
      Features features, ConfigurationEntry entry, PerformanceTuple perfTuple) {
    double predicted = perfPred.predictPerformance(features, entry.getConfig());
    double actual = perfTuple.getPerformance();
    return Math.abs(actual - predicted)
        / (Double.valueOf(actual).equals(0.) ? 1 : Math.abs(actual));
  }
}