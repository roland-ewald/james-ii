/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * UniformDistribution -
 * 
 * This class helps to generate uniform distributed random numbers
 * 
 * By default a random number between 0 and 1 will be returned!.
 * 
 * @author Roland Ewald
 */
public class UniformDistribution extends AbstractDistribution {

  /** Serialization ID. */
  private static final long serialVersionUID = -2801841236222076892L;

  /** The lower border. */
  private double lowerBorder = 0;

  /** The upper border (exclusive). */
  private double upperBorder = 1;

  /**
   * Instantiates a new uniform distribution.
   * 
   * @param seed
   *          the seed
   */
  public UniformDistribution(long seed) {
    super(seed);
  }

  /**
   * The Constructor.
   * 
   * @param randomizer
   *          the randomizer
   */
  public UniformDistribution(IRandom randomizer) {
    super(randomizer);
  }

  /**
   * Standard constructor.
   * 
   * @param randomizer
   *          random number generator
   * @param lowerBorder
   *          the lower border of the value range
   * @param upperBorder
   *          the upper border of the value range (exclusive)
   */
  public UniformDistribution(IRandom randomizer, double lowerBorder,
      double upperBorder) {
    super(randomizer);
    setLowerBorder(lowerBorder);
    setUpperBorder(upperBorder);
  }

  /**
   * Get lower border.
   * 
   * @return double the lower border
   */
  public double getLowerBorder() {
    return lowerBorder;
  }

  /**
   * Gets the random number.
   * 
   * @return the random number
   * 
   * @see org.jamesii.core.math.random.distributions.AbstractDistribution#getRandomNumber()
   */
  @Override
  public double getRandomNumber() {
    return lowerBorder + (upperBorder - lowerBorder) * getRandom().nextDouble();
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new UniformDistribution(newRandomizer, lowerBorder, upperBorder);
  }

  /**
   * Get upper border.
   * 
   * @return double the upper border
   */
  public double getUpperBorder() {
    return upperBorder;
  }

  /**
   * Set lower border.
   * 
   * @param lowerBorder
   *          the lower border
   */
  public final void setLowerBorder(double lowerBorder) {
    this.lowerBorder = lowerBorder;
  }

  /**
   * Set upper border.
   * 
   * @param upperBorder
   *          the new upper border
   */
  public final void setUpperBorder(double upperBorder) {
    this.upperBorder = upperBorder;
  }

  @Override
  public String toString() {
    return "uniform(" + lowerBorder + "," + upperBorder + ")";
  }

}
