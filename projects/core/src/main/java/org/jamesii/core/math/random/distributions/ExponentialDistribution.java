/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * ExpRand -
 * 
 * Exponential distribution
 * 
 * ATTENTION: The scale parameter mu must NOT be confused with the rate
 * parameter lambda, which is 1/mu. So, when initializing this distribution with
 * a rate parameter, use setMu(1/rateParameter).
 */
public class ExponentialDistribution extends AbstractDistribution {

  /** The constant Serialisation ID. */
  private static final long serialVersionUID = -3530176573323326998L;

  /**
   * Scale parameter of distribution (not to be confused with the rate
   * parameter!).
   */
  private double mu;

  /**
   * see Law/Kelton "Simulation, modeling and analysis" (third edition) p. 460
   * <p>
   * Uses internal random number generator.
   * 
   * @param pmu
   *          the pmu
   * 
   * @return get a exponentially distributed random number
   */
  public final double expRandVar(double pmu) {
    return -pmu * Math.log(getRandom().nextDouble());
  }

  /**
   * The Constructor.
   * 
   * @param seed
   *          the seed
   */
  public ExponentialDistribution(long seed) {
    super(seed);
    this.mu = 1.;
  }

  /**
   * The Constructor.
   * 
   * @param random
   *          the random
   */
  public ExponentialDistribution(IRandom random) {
    super(random);
    this.mu = 1.;
  }

  /**
   * The Constructor.
   * 
   * @param randomizer
   *          the randomizer
   * @param mu
   *          the mu
   */
  public ExponentialDistribution(IRandom randomizer, double mu) {
    super(randomizer);
    this.mu = mu;
  }

  /**
   * Get scale parameter mu (mu = 1/lambda).
   * 
   * @return the mu
   */
  public double getMu() {
    return mu;
  }

  @Override
  public double getRandomNumber() {
    return -mu * Math.log(getRandom().nextDouble());
  }

  /**
   * Gets the random number.
   * 
   * @param customMu
   *          the custom mu
   * 
   * @return the random number
   */
  public double getRandomNumber(double customMu) {
    return -customMu * Math.log(getRandom().nextDouble());
  }

  /**
   * Gets the similar.
   * 
   * @param newRandomizer
   *          the new randomizer
   * 
   * @return the similar
   * 
   * @see org.jamesii.core.math.random.distributions.AbstractDistribution#getSimilar()
   */
  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new ExponentialDistribution(newRandomizer, mu);
  }

  /**
   * Set scale parameter mu (mu = 1/lambda).
   * 
   * @param mu
   *          the mu
   */
  public void setMu(double mu) {
    this.mu = mu;
  }

  @Override
  public String toString() {
    return "exponential(" + mu + ")";
  }

}
