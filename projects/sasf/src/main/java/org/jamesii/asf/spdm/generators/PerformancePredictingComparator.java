/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;

/**
 * A comparator for {@link ConfigurationEntry}. Applies a given
 * {@link IPerformancePredictor} and considers the {@link Features} of a single
 * problem.
 * 
 * @author Roland Ewald
 */
public class PerformancePredictingComparator implements
    Comparator<ConfigurationEntry>, Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6428515900598373636L;

  /** The problem features. */
  private final Features features;

  /** The performance predictor. */
  private final IPerformancePredictor predictor;

  /**
   * The cache for past predictions. This is necessary in case the predictor
   * relies on some random elements (e.g. some form of random choice), in order
   * to comply to the general contract of {@link Comparator}.
   */
  private final Map<Configuration, Double> pastPredictions =
      new HashMap<>();

  /**
   * Create a new performance predicting comparator.
   * 
   * @param problemFeatures
   *          the problem features
   * @param performancePredictor
   *          the performance predictor
   */
  public PerformancePredictingComparator(Features problemFeatures,
      IPerformancePredictor performancePredictor) {
    features = problemFeatures;
    predictor = performancePredictor;
  }

  @Override
  public int compare(ConfigurationEntry entry1, ConfigurationEntry entry2) {
    double prediction1 = lookUp(entry1);
    double prediction2 = lookUp(entry2);
    int compResult = Double.compare(prediction1, prediction2);
    // If comparison is undecided, at least be consistent and deterministic
    if (compResult == 0) {
      compResult =
          Integer.valueOf(entry1.hashCode()).compareTo(entry2.hashCode());
    }
    // The higher the predicted performance, the better
    return -1 * compResult;
  }

  /**
   * Look up prediction in cache, if non-existent predict performance.
   * 
   * @param entry
   *          the configuration entry
   * @return the predicted performance
   */
  private double lookUp(ConfigurationEntry entry) {
    Configuration config = entry.getConfig();
    if (!pastPredictions.containsKey(config)) {
      pastPredictions.put(config,
          Double.valueOf(predictor.predictPerformance(features, config)));
    }
    return pastPredictions.get(config);
  }

}
