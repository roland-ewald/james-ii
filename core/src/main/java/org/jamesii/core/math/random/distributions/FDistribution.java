/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Implements the F-Distribution.
 * 
 * @author Felix Willud
 */
public class FDistribution extends AbstractDistribution {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7474622911488321879L;

  /** The default value for {@link #d1} and {@link #d2}. */
  private static final int DEFAULT_DEGREE_FREEDOM = 20;

  /** First degree of freedom. */
  private int d1 = DEFAULT_DEGREE_FREEDOM;

  /** Second degree of freedom. */
  private int d2 = DEFAULT_DEGREE_FREEDOM;

  /**
   * Instantiates a new F distribution.
   * 
   * @param random
   *          the random number generator
   */
  public FDistribution(IRandom random) {
    super(random);
  }

  /**
   * Instantiates a new F distribution.
   * 
   * @param random
   *          the random
   * @param d1
   *          first degree of freedom
   * @param d2
   *          second degree of freedom
   */
  public FDistribution(IRandom random, int d1, int d2) {
    super(random);
    this.d1 = d1;
    this.d2 = d2;
  }

  /**
   * Instantiates a new F distribution.
   * 
   * @param seed
   *          the seed
   */
  public FDistribution(long seed) {
    super(seed);
  }

  @Override
  public double getRandomNumber() {
    ChiSquareDistribution chi1 = new ChiSquareDistribution(getRandom());
    ChiSquareDistribution chi2 = new ChiSquareDistribution(getRandom());
    double chirand1 = 0.0;
    double chirand2 = 0.0;

    chi1.setDegreeOfFreedom(d1);
    chirand1 = chi1.getRandomNumber();
    chi2.setDegreeOfFreedom(d2);
    chirand2 = chi2.getRandomNumber();
    return (chirand1 / d1) / (chirand2 / d2);
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new FDistribution(newRandomizer);
  }

  /**
   * Sets the parameter alpha (mean).
   * 
   * @param d1
   *          first degree of freedom
   */

  public void setDegreeOfFreedomOne(int d1) {
    this.d1 = d1;
  }

  /**
   * Sets the parameter beta (mean).
   * 
   * @param d2
   *          second degree of freedom
   */

  public void setDegreeOfFreedomTwo(int d2) {
    this.d2 = d2;
  }

}