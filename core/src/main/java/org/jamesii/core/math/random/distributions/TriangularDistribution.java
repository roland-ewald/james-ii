/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * example<br>
 * /|<br>
 * / |<br>
 * / |<br>
 * / |<br>
 * -----<br>
 * .
 * 
 * @author Jan Himmelspach
 */
public class TriangularDistribution extends AbstractDistribution {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1903860090621239883L;

  /** The width of the distribution. */
  private double width = 1.5;

  /**
   * Instantiates a new triangular distribution.
   * 
   * @param seed
   *          the seed
   */
  public TriangularDistribution(long seed) {
    super(seed);
  }

  /**
   * Instantiates a new triangular distribution.
   * 
   * @param randomizer
   *          the randomizer
   */
  public TriangularDistribution(IRandom randomizer) {
    super(randomizer);
  }

  /**
   * Instantiates a new triangular distribution.
   * 
   * @param randomizer
   *          the randomizer
   * @param width
   *          the width
   */
  public TriangularDistribution(IRandom randomizer, double width) {
    this(randomizer);
    this.width = width;
  }

  @Override
  public double getRandomNumber() {
    return width * Math.pow(getRandom().nextDouble(), 0.5);
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return "T(" + width + ")";
  }

}
