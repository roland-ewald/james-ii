/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Superclass generalizing the (two) normal distributions already present.
 * 
 * Subclasses should differ only in their getNextGaussian() method that
 * generates numbers in N(0,1). The transformation to N(mean,dev) and setting of
 * mean and deviation takes place here. getNextGaussian() is now also accessible
 * for other distributions that rely on numbers from N(0,1), for example
 * {@link InverseGaussianDistribution}, {@link ChiSquareDistribution} or
 * {@link MultipleNormalDistribution}, which can now use either of the
 * implemented normal distributions.
 * 
 * 
 * 
 * @author Arne Bittig
 * @date 10.02.2011
 * 
 */
public abstract class AbstractNormalDistribution extends AbstractDistribution {

  /** Serialization ID */
  private static final long serialVersionUID = -8156278759206169530L;

  /** Value deviation. */
  private double deviation;

  /** Value mean. */
  private double mean;

  /**
   * @param seed
   */
  public AbstractNormalDistribution(long seed) {
    super(seed);
    setMean(0);
    setDeviation(1);
  }

  /**
   * @param randomizer
   */
  public AbstractNormalDistribution(IRandom randomizer) {
    super(randomizer);
    setMean(0);
    setDeviation(1);
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
  public AbstractNormalDistribution(IRandom randomizer, double mean,
      double deviation) {
    super(randomizer);
    setMean(mean);
    setDeviation(deviation);
  }

  /**
   * Get deviation.
   * 
   * @return deviation
   */
  public double getDeviation() {
    return deviation;
  }

  /**
   * Get mean.
   * 
   * @return mean
   */
  public final double getMean() {
    return mean;
  }

  /**
   * Set deviation.
   * 
   * @param deviation
   *          the deviation
   */
  public final void setDeviation(double deviation) {
    if (Double.compare(deviation, 0) >= 0) {
      {
        this.deviation = deviation;
      }
    }
    // TODO? else throw IllegalArgumentException? (ab358)
  }

  /**
   * Set mean.
   * 
   * @param mean
   *          the mean
   */
  public final void setMean(double mean) {
    this.mean = mean;
  }

  /**
   * Generate random number from N(0,1). Transformation to N(mean,dev) should
   * take place elsewhere, i.e. in getRandomNumber(), such that this function
   * can be used in other distributions that rely on a number from N(0,1).
   * 
   * @return The next normal-N(0,1)-distributed random number.
   */
  protected abstract double getNextGaussian();

  /**
   * Gets the random number.
   * 
   * @return the random number
   * 
   * @see org.jamesii.core.math.random.distributions.AbstractDistribution#getRandomNumber()
   */
  @Override
  public double getRandomNumber() {
    return mean + (getNextGaussian() * deviation);
  }

  @Override
  public String toString() {
    return "N(" + mean + "," + deviation + ")";
  }

}