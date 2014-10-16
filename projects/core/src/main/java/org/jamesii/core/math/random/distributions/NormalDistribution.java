/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * NormalDistribution -
 * 
 * This class helps to generate normal distributed random numbers.
 * 
 * @author Roland Ewald
 */
public class NormalDistribution extends AbstractNormalDistribution {

  /** Serialization ID. */
  private static final long serialVersionUID = -3003543767983550114L;

  /** Cache for the second generated number. */
  private double nextGaussian;

  /** Indicates whether a previously computed number is available. */
  private boolean gotNextGaussian;

  /**
   * Instantiates a new normal distribution.
   * 
   * @param seed
   *          the seed
   */
  public NormalDistribution(long seed) {
    super(seed);
  }

  /**
   * Instantiates a new normal distribution.
   * 
   * @param random
   *          the random
   */
  public NormalDistribution(IRandom random) {
    super(random);
  }

  /**
   * Standard constructor.
   * 
   * @param randomizer
   *          random number generator
   * @param mean
   *          mean value
   * @param deviation
   *          standard deviation
   */
  public NormalDistribution(IRandom randomizer, double mean, double deviation) {
    super(randomizer, mean, deviation);
  }

  /**
   * This computes two normal-distributed numbers with Marsaglia's polar method.
   * <p>
   * <strong>NOTE:</strong> This method may get into an infinite loop depending
   * on the quality of the RNG used. In some circumstances this can happen,
   * particularly with very poorly-chosen parameters.
   * 
   * @return The next normal-distributed random number.
   */
  @Override
  public double getNextGaussian() {
    if (gotNextGaussian) {
      gotNextGaussian = false;
      return nextGaussian;
    }
    // we don't have a number ready, so compute two more.
    double v1, v2, s;
    do {
      v1 = 2 * getRandom().nextDouble() - 1; // between -1 and 1
      v2 = 2 * getRandom().nextDouble() - 1; // between -1 and 1
      s = v1 * v1 + v2 * v2;
    } while (s >= 1 || s == 0);
    double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
    nextGaussian = v2 * multiplier;
    gotNextGaussian = true;
    return v1 * multiplier;
  }

  @Override
  public IDistribution getSimilar(IRandom newRandomizer) {
    return new NormalDistribution(newRandomizer, getMean(), getDeviation());
  }
}
