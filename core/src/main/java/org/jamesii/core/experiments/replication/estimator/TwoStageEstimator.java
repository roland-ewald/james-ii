/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication.estimator;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.Variance;
import org.jamesii.core.util.misc.Pair;

/**
 * Estimates the amount of replications in a two-stage procedure ( based on an
 * initial sample), for a given confidence and error tolerance.
 * 
 * @see Soren Asmussen and Peter W. Glynn: Stochastic Simulation, Springer, 2008
 *      p. 71-73
 * 
 * @author Stefan Leye
 */
public abstract class TwoStageEstimator implements
    IRequiredReplicationsEstimator {

  /**
   * The allowed error.
   */
  private double allowedError;

  /**
   * Flag determining whether the error is absolute or relative.
   */
  private boolean absolute;

  /**
   * The desired confidence.
   */
  private double confidence;

  /**
   * Basic constructor.
   * 
   * @param allowedError
   *          the allowed error.
   * @param absolute
   *          flag determining whether the error is absolute or relative
   * @param confidence
   *          the desired confidence
   */
  public TwoStageEstimator(double allowedError, boolean absolute,
      double confidence) {
    this.allowedError = allowedError;
    this.confidence = confidence;
    this.absolute = absolute;
  }

  @Override
  public int estimateAditionalReplications(List<? extends Number> sample) {
    int size = sample.size();
    if (size == 0) {
      return 1;
    }
    Pair<Double, Double> stats = Variance.varianceAndAM(sample);
    Double variance = stats.getFirstValue();
    // System.out.println("Variance: " + variance);
    Double mean = stats.getSecondValue();
    // System.out.println("Mean: " + mean);
    if (mean.compareTo(0.0) == 0) {
      // TODO does this make sense?
      mean = 1.0;
    }
    double alpha = 1 - confidence;
    double p = 1 - alpha / 2;
    Double q = getQuantil(p, sample);
    // System.out.println("t: " + t);
    Double newReps = q / getAbsoluteError(mean);
    newReps *= newReps * variance;
    newReps += 1;
    // System.out.println("new reps: " + newReps);
    return (int) (newReps - size);
  }

  private double getAbsoluteError(double mean) {
    if (absolute) {
      return allowedError;
    }
    return allowedError * mean;
  }

  /**
   * Returns the p-1 of the approximated underlying distribution of the mean.
   * 
   * @param sample
   *          the initial sample
   * @param p
   *          the p value
   * @return the p-1 quantil of the underlying distribution
   */
  protected abstract double getQuantil(double p, List<? extends Number> sample);
}
