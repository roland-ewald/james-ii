/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Cauchy-Lorentz-Distribution has two parameters: s > 0 -infinite < t <
 * infinite
 * 
 * t serves as offset and 1/s defines density.
 * 
 * @author Simon/sb513
 */
public class CauchyDistribution extends AbstractDistribution {

  /** The s. */

  /** Standard-Cauchy-Distribution */
  private double s = 1;

  /** The t. */
  private double t = 0;

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3235824234777726452L;

  /**
   * Instantiates a new cauchy distribution.
   * 
   * @param seed
   *          the seed
   */
  public CauchyDistribution(long seed) {
    super(seed);
  }

  /**
   * Instantiates a new cauchy distribution.
   * 
   * @param random
   *          the random
   */
  public CauchyDistribution(IRandom random) {
    super(random);
  }

  /**
   * Instantiates a new cauchy distribution.
   * 
   * @param random
   *          the random
   * @param nt
   *          the nt
   * @param ns
   *          the ns
   */
  public CauchyDistribution(IRandom random, double nt, double ns) {
    super(random);
    s = ns;
    t = nt;
  }

  /**
   * Instantiates a new cauchy distribution.
   * 
   * @param seed
   *          the seed
   * @param nt
   *          the nt
   * @param ns
   *          the ns
   */
  public CauchyDistribution(long seed, double nt, double ns) {
    super(seed);
    s = ns;
    t = nt;
  }

  @Override
  public double getRandomNumber() {
    double r = getRandom().nextDouble();
    return s * Math.tan(Math.PI * (r - 0.5)) + t;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new CauchyDistribution(newRandomizer, t, s);
  }

}
