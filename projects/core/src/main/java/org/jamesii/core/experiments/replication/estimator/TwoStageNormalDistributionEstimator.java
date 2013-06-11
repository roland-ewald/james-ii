/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication.estimator;

import java.util.List;

import org.jamesii.core.math.statistics.univariate.InverseNormalDistributionFunction;

/**
 * Estimates the additional amount of required replications based on the normal
 * distribution.
 * 
 * @author Stefan Leye
 */
public class TwoStageNormalDistributionEstimator extends TwoStageEstimator {

  /**
   * Basic constructor.
   * 
   * @param allowedError
   *          the allowed error.
   * @param confidence
   *          the desired confidence
   */
  public TwoStageNormalDistributionEstimator(double allowedError,
      boolean absolute, double confidence) {
    super(allowedError, absolute, confidence);
  }

  @Override
  protected double getQuantil(double p, List<? extends Number> sample) {
    return InverseNormalDistributionFunction.quantil(p, true);
  }

}
