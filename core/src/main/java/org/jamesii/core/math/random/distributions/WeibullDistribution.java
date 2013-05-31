/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Implements the Weibull distribution.
 * 
 * @author Felix Willud
 */
public class WeibullDistribution extends AbstractDistribution {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2216321808724830211L;

  /** Default value for {@link #alpha}. */
  private static final double DEFAULT_ALPHA = 3.0;

  /** Default value for {@link #beta}. */
  private static final double DEFAULT_BETA = 5.0;

  /** Mean of Distribution. */
  private double alpha = DEFAULT_ALPHA;

  /** The beta. */
  private double beta = DEFAULT_BETA;

  /**
   * Instantiates a new weibull distribution.
   * 
   * @param random
   *          the random
   */
  public WeibullDistribution(IRandom random) {
    super(random);
  }

  /**
   * Instantiates a new weibull distribution.
   * 
   * @param random
   *          the random
   * @param alpha
   *          the alpha
   * @param beta
   *          the beta
   */
  public WeibullDistribution(IRandom random, double alpha, double beta) {
    super(random);
    this.alpha = alpha;
    this.beta = beta;
  }

  /**
   * Instantiates a new weibull distribution.
   * 
   * @param seed
   *          the seed
   */
  public WeibullDistribution(long seed) {
    super(seed);
  }

  @Override
  public double getRandomNumber() {
    double r;
    do {
      r = getRandom().nextDouble();
    } while (r <= 0.0);
    return alpha * Math.pow(-Math.log(r), 1 / beta);
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new WeibullDistribution(newRandomizer);
  }

  /**
   * Sets the parameter alpha (mean).
   * 
   * @param alpha
   *          the alpha
   */

  public void setAlpha(double alpha) {
    this.alpha = alpha;
  }

  /**
   * Sets the parameter beta (mean).
   * 
   * @param beta
   *          the beta
   */

  public void setBeta(double beta) {
    this.beta = beta;
  }

}
