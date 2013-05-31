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

public class CrossValidationEstimator extends SequentialEstimator {

  /**
   * The allowed deviation between the means.
   */
  private double allowedDeviation;

  /**
   * The count of following stages, the allowed error has to be undercut to meet
   * the criterion.
   */
  private int stages;

  public CrossValidationEstimator(int additionalReplicationCount,
      double allowedDeviation, int stages) {
    super(additionalReplicationCount);
    if (stages < additionalReplicationCount) {
      setAdditionalReplicationCount(stages);
    }
    this.allowedDeviation = allowedDeviation;
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
    Double referenceMean = mean;
    for (int i = size - stages; i < size; i++) {
      Double value = sample.get(i).doubleValue();
      variance = Variance.varianceAddEntry(variance, mean, value, i + 1);
      mean = ArithmeticMean.arithmeticMean(mean, value, i + 1);
      Double deviation = Math.abs(mean - referenceMean);
      if (referenceMean.compareTo(0.0) == 0) {
        deviation /= mean;
      } else {
        deviation /= referenceMean;
      }
      if (deviation.compareTo(allowedDeviation) > 0) {
        return false;
      }
    }
    return true;
  }

}
