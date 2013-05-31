/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * This class helps to generate Erlang distributed random numbers.
 * 
 * By default the degree of freedom n is 3 and lambda is 1!
 * 
 * @author tf081
 */
public class ErlangDistribution extends AbstractDistribution {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2689514020603725284L;

  /** Parameter lambda. */
  private double lambda = 1;

  /** Degree of Freedom. */
  private int degreeOfFreedom = 3;

  /**
   * Instantiates a new erlang distribution.
   * 
   * @param seed
   *          the seed
   */
  public ErlangDistribution(long seed) {
    super(seed);
  }

  /**
   * The Constructor.
   * 
   * @param randomizer
   *          the randomizer
   */
  public ErlangDistribution(IRandom randomizer) {
    super(randomizer);
  }

  /**
   * Standard constructor.
   * 
   * @param randomizer
   *          Random number generator
   * @param lam
   *          Lambda for the Erlang Distribution
   * @param degOfFreedom
   *          Degree of Freedom for the Erlang Distribution
   */
  public ErlangDistribution(IRandom randomizer, double lam, int degOfFreedom) {
    super(randomizer);
    setLambda(lam);
    setDegreeOfFreedom(degOfFreedom);
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

    double erlNumber = 1;

    for (int i = 1; i <= degreeOfFreedom; i++) {
      erlNumber *= getRandom().nextDouble();
    }

    erlNumber = -StrictMath.log(erlNumber) / lambda;

    return erlNumber;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new ErlangDistribution(newRandomizer, lambda, degreeOfFreedom);
  }

  /**
   * Get Degree of Freedom.
   * 
   * @return int the Degree of Freedom
   */
  public int getDegreeOfFreedom() {
    return degreeOfFreedom;
  }

  /**
   * Get Lambda.
   * 
   * @return double lambda
   */
  public final double getLambda() {
    return lambda;
  }

  /**
   * Set Degree of Freedom.
   * 
   * @param degreeOfFreedom
   *          new Degree of Freedom for distribution
   */
  public final void setDegreeOfFreedom(int degreeOfFreedom) {
    if (degreeOfFreedom > 0) {
      this.degreeOfFreedom = degreeOfFreedom;
    }
  }

  /**
   * Set Lambda.
   * 
   * @param lam
   *          new Lambda for distribution
   */
  public final void setLambda(double lam) {
    this.lambda = lam;
  }
}
