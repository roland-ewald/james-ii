/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication.estimator;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.InverseStudentsTDistributionFunction;

/**
 * Estimates the additional amount of required replications based on the
 * students t distribution.
 * 
 * @author Stefan Leye
 */
public class TwoStageStudentsTDistributionEstimator extends TwoStageEstimator {

  /**
   * Basic constructor.
   * 
   * @param allowedError
   *          the allowed error.
   * @param confidence
   *          the desired confidence
   */
  public TwoStageStudentsTDistributionEstimator(double allowedError,
      boolean absolute, double confidence) {
    super(allowedError, absolute, confidence);
  }

  @Override
  protected double getQuantil(double p, List<? extends Number> sample) {
    return InverseStudentsTDistributionFunction.quantil(p, sample.size() - 1);
  }

}
