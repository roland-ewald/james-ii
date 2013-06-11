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
 * |\<br>
 * | \<br>
 * | \<br>
 * | \<br>
 * -----<br>
 * .
 * 
 * @author Jan Himmelspach
 */
public class NegativeTriangularDistribution extends AbstractDistribution {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1027778491181746434L;

  /** The width of the distribution. */
  private double width = 1.5;

  /**
   * Instantiates a new negative triangular distribution.
   * 
   * @param seed
   *          the seed
   */
  public NegativeTriangularDistribution(long seed) {
    super(seed);
  }

  /**
   * Instantiates a new negative triangular distribution.
   * 
   * @param randomizer
   *          the randomizer
   */
  public NegativeTriangularDistribution(IRandom randomizer) {
    super(randomizer);
  }

  /**
   * Instantiates a new negative triangular distribution.
   * 
   * @param randomizer
   *          the randomizer
   * @param width
   *          the width
   */
  public NegativeTriangularDistribution(IRandom randomizer, double width) {
    this(randomizer);
    this.width = width;
  }

  @Override
  public double getRandomNumber() {
    return width * (1 - Math.sqrt(1 - getRandom().nextDouble()));
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return "nT(" + width + ")";
  }

}
