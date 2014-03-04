/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.perfmeasures;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.ConfigurationEntry;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.util.PerformanceTuples;
import org.jamesii.core.util.misc.Pair;


/**
 * Simply adds up all wrong comparison predictions.
 * 
 * @author Roland Ewald
 */
public class SimpleBooleanPerfMeasure implements IPredictorPerformanceMeasure {

  @Override
  public <T extends PerformanceTuple> Pair<Double, Integer> calculatePerformance(
      List<T> testData, IPerformancePredictor predictor) {

    Map<Features, List<T>> testTuples =
        PerformanceTuples.sortToFeatureMap(testData);

    // Compare performance tuples with prediction for each test data
    double errorSum = 0.0;
    int predSum = 0;
    for (Entry<Features, List<T>> testFeatureEntry : testTuples.entrySet()) {
      Pair<Double, Integer> error =
          calculateError(testFeatureEntry.getKey(),
              testFeatureEntry.getValue(), predictor);
      errorSum += error.getFirstValue();
      predSum += error.getSecondValue();
    }

    return new Pair<>(errorSum, predSum);
  }

  /**
   * Iterates over (a shuffled) list of performance tuples and calculates the
   * error when comparing two of them.
   * 
   * @param features
   *          the features for the predictor
   * @param testTuples
   *          test data for this feature
   * @param predictor
   *          predictor to be evaluated
   * @return
   */
  protected <T extends PerformanceTuple> Pair<Double, Integer> calculateError(
      Features features, List<T> testTuples, IPerformancePredictor predictor) {

    double errors = 0;
    int predCount = 0;
    Collections.shuffle(testTuples);

    for (int i = 0; i < testTuples.size() - 1; i++) {
      for (int j = i + 1; j < testTuples.size(); j++) {
        ConfigurationEntry entry1 =
            new ConfigurationEntry(testTuples.get(i).getConfiguration());
        ConfigurationEntry entry2 =
            new ConfigurationEntry(testTuples.get(j).getConfiguration());
        predCount++;
        errors +=
            calculateError(predictor, features, entry1, entry2,
                testTuples.get(i), testTuples.get(i + 1));
      }
    }

    return new Pair<>(errors, predCount);
  }

  /**
   * Calculates error. Here, an outcome different to the direct comparison of
   * performance tuples will be penalised with 1, otherwise the method returns
   * 0.
   * 
   * @param predictor
   *          the predictor to be tested
   * @param features
   *          the problem features
   * @param entry1
   *          the first configuration entry
   * @param entry2
   *          the second configuration entry
   * @param performanceTuple1
   *          the first performance tuple
   * @param performanceTuple2
   *          the second performance tuple
   * @return the double
   */
  protected double calculateError(IPerformancePredictor predictor,
      Features features, ConfigurationEntry entry1, ConfigurationEntry entry2,
      PerformanceTuple performanceTuple1, PerformanceTuple performanceTuple2) {
    int predictedComparison =
        Double.valueOf(
            predictor.predictPerformance(features, entry1.getConfig()))
            .compareTo(
                predictor.predictPerformance(features, entry2.getConfig()));
    int trueComparison =
        Double.valueOf(performanceTuple1.getPerformance()).compareTo(
            performanceTuple2.getPerformance());
    return !Float.valueOf(Math.signum(predictedComparison)).equals(
        Math.signum(trueComparison)) ? 1 : 0;
  }
}
