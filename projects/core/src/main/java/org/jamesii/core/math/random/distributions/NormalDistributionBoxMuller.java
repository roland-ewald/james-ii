/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Normal distribution. The values are generated using the Box-Muller method.
 * 
 * @author Johannes Rössel
 */
public class NormalDistributionBoxMuller extends AbstractNormalDistribution {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3003543767983550114L;

  /**
   * Instantiates a new normal distribution.
   * 
   * @param seed
   *          the seed
   */
  public NormalDistributionBoxMuller(long seed) {
    super(seed);
  }

  /**
   * Instantiates a new normal distribution.
   * 
   * @param random
   *          the random
   */
  public NormalDistributionBoxMuller(IRandom random) {
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
  public NormalDistributionBoxMuller(IRandom randomizer, double mean,
      double deviation) {
    super(randomizer, mean, deviation);
  }

  /** Cache for the second generated number. */
  private double nextGaussian;

  /** Indicates whether a previously computed number is available. */
  private boolean gotNextGaussian;

  /**
   * This computes two normal-distributed numbers with Marsaglia's polar method.
   * <p>
   * Unlike Marsaglia's polar method the Box-Muller method does not use
   * rejection sampling. This is why this method won't ever run into an infinite
   * loop, even with very low-quality generators. It is slower, however.
   * 
   * @return The next normal-distributed random number.
   */
  @Override
  protected double getNextGaussian() {
    if (gotNextGaussian) {
      gotNextGaussian = false;
      return nextGaussian;
    }

    double x, y;
    x = StrictMath.sqrt(-2 * StrictMath.log(getRandom().nextDouble()));
    y = 2 * StrictMath.PI * getRandom().nextDouble();

    double r1, r2;
    r1 = x * StrictMath.cos(y);
    r2 = x * StrictMath.sin(y);

    nextGaussian = r2;
    gotNextGaussian = true;
    return r1;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new NormalDistributionBoxMuller(newRandomizer, getMean(),
        getDeviation());
  }
}
