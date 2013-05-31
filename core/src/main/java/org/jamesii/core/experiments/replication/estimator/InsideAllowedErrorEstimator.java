/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication.estimator;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.core.math.statistics.univariate.Variance;

/**
 * Estimates the count of required replication with a constant until the
 * standard deviation under-cuts a given count of following stages.
 * 
 * @author Stefan Leye
 */
public class InsideAllowedErrorEstimator extends SequentialEstimator {

  /**
   * The allowed error.
   */
  private double allowedError;

  /**
   * The count of following stages, the allowed error has to be undercut to meet
   * the criterion.
   */
  private int stages;

  /**
   * Basic constructor.
   * 
   * @param additionalReplicationCount
   *          the count of additional replications if the criterion is not met
   * @param allowedError
   *          the allowed error which has to be under-cut
   * @param stages
   *          the count of stages the allowed error has to be under-cut
   */
  public InsideAllowedErrorEstimator(int additionalReplicationCount,
      double allowedError, int stages) {
    super(additionalReplicationCount);
    if (stages < additionalReplicationCount) {
      setAdditionalReplicationCount(stages);
    }
    this.allowedError = allowedError;
    this.stages = stages;
  }

  @Override
  protected boolean metCriterion(List<? extends Number> sample) {
    int size = sample.size();
    if (size < 2) {
      return false;
    }
    double variance = 0.0;
    double mean = 0.0;
    for (int i = 0; i < size - stages; i++) {
      Double value = sample.get(i).doubleValue();
      variance = Variance.varianceAddEntry(variance, mean, value, i + 1);
      mean = ArithmeticMean.arithmeticMean(mean, value, i + 1);
    }
    for (int i = size - stages; i < size; i++) {
      Double value = sample.get(i).doubleValue();
      variance = Variance.varianceAddEntry(variance, mean, value, i + 1);
      mean = ArithmeticMean.arithmeticMean(mean, value, i + 1);
      double std = Math.sqrt(variance);
      Double relativeError = std / mean;
      if (relativeError.compareTo(allowedError) > 0) {
        return false;
      }
    }
    return true;

  }

}
