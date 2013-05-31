/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Gamma distribution.
 * 
 * @author Jan Himmelspach
 */
public class GammaDistribution extends AbstractDistribution {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6206156315748568528L;

  /** The order. */
  private int order = 1;

  /**
   * Instantiates a new gamma distribution.
   * 
   * @param seed
   *          the seed
   */
  public GammaDistribution(Long seed) {
    super(seed);
  }

  /**
   * Instantiates a new gamma distribution.
   * 
   * @param randomizer
   *          the randomizer
   */
  public GammaDistribution(IRandom randomizer) {
    super(randomizer);
  }

  /**
   * Instantiates a new gamma distribution.
   * 
   * @param randomizer
   *          the randomizer
   * @param order
   *          the order
   */
  public GammaDistribution(IRandom randomizer, int order) {
    super(randomizer);
    this.order = order;
  }

  /**
   * Gets the gamma.
   * 
   * @param randomizer
   *          the randomizer
   * @param v
   *          the v
   * 
   * @return the gamma
   */
  public static double getGamma(IRandom randomizer, int v) {
    double result = 1.0;
    for (int i = 0; i < v; i++) {
      result *= randomizer.nextDouble();
    }
    return -Math.log(result);
  }

  @Override
  public double getRandomNumber() {
    return getGamma(getRandom(), order);
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    // TODO Auto-generated method stub
    return null;
  }

}
