/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * LaPlace/DoubleExponential Distribution has two parameters sigma and mu lambda
 * = 1/sigma sigma : deviation mu: mean.
 * 
 * @author Simon/sb513
 */

public class LaPlaceDistribution extends AbstractDistribution {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4657221982379514450L;

  /** The mu. */
  private double mu = 0;

  /** The sigma. */
  private double sigma = 1;

  /**
   * Instantiates a new la place distribution.
   * 
   * @param seed
   *          the seed
   */
  public LaPlaceDistribution(long seed) {
    super(seed);
  }

  /**
   * Instantiates a new la place distribution.
   * 
   * @param random
   *          the random
   */
  public LaPlaceDistribution(IRandom random) {
    super(random);
  }

  /**
   * Instantiates a new la place distribution.
   * 
   * @param random
   *          the random
   * @param nsigma
   *          the nsigma
   * @param nmu
   *          the nmu
   */
  public LaPlaceDistribution(IRandom random, double nsigma, double nmu) {
    super(random);
    sigma = nsigma;
    mu = nmu;
  }

  /**
   * Instantiates a new la place distribution.
   * 
   * @param seed
   *          the seed
   * @param nsigma
   *          the nsigma
   * @param nmu
   *          the nmu
   */
  public LaPlaceDistribution(long seed, double nsigma, double nmu) {
    super(seed);
    sigma = nsigma;
    mu = nmu;
  }

  @Override
  public double getRandomNumber() {
    double retval = 0;
    double r = getRandom().nextDouble();
    if (r < 0.5) {
      retval = sigma * Math.log(2 * r) + mu;
    } else {
      retval = -sigma * Math.log(2 - 2 * r) + mu;
    }

    // this code is equal:
    // retval = mu - sigma * Math.signum(r - 0.5) * Math.log(1 - 2*Math.abs(r -
    // 0.5));

    return retval;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new LaPlaceDistribution(newRandomizer, sigma, mu);
  }

}
