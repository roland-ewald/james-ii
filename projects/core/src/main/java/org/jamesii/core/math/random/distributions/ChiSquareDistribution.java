/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * This class helps to generate Chi-square distributed random numbers.
 * 
 * By default the degree of freedom is 3!
 * 
 * @author tf081
 */
public class ChiSquareDistribution extends AbstractDistribution {

  /** Serialization ID. */
  private static final long serialVersionUID = 0L;

  /** Degree of Freedom. */
  private int degreeOfFreedom = 3;

  /** NormalDistribution to create a Chi-square distribution. */
  private AbstractNormalDistribution normDist;

  /**
   * Instantiates a new chi square distribution.
   * 
   * @param seed
   *          the seed
   */
  public ChiSquareDistribution(long seed) {
    super(seed);
    normDist = new NormalDistribution(seed);
  }

  /**
   * The Constructor.
   * 
   * @param randomizer
   *          the randomizer
   */
  public ChiSquareDistribution(IRandom randomizer) {
    super(randomizer);
    normDist = new NormalDistribution(randomizer);
  }

  /**
   * Standard constructor.
   * 
   * @param randomizer
   *          random number generator
   * @param degOfFreedom
   *          Degree of Freedom for the Chi-square Distribution
   */
  public ChiSquareDistribution(IRandom randomizer, int degOfFreedom) {
    super(randomizer);
    normDist = new NormalDistribution(randomizer);
    setDegreeOfFreedom(degOfFreedom);
  }

  /**
   * Extended constructor also allowing customization of the used gaussian (i.e.
   * normal) distribution
   * 
   * @param randomizer
   *          random number generator
   * @param degOfFreedom
   *          Degree of Freedom for the Chi-square Distribution
   * @param normDist
   *          Normal distribution to use internally
   */
  public ChiSquareDistribution(IRandom randomizer, int degOfFreedom,
      AbstractNormalDistribution normDist) {
    super(randomizer);
    this.normDist = normDist;
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

    // Chi-Square random number
    double chiSquareNumber = 0;

    // Gaussian random number
    double gaussianNumber;

    for (int i = 1; i <= degreeOfFreedom; i++) {
      gaussianNumber = normDist.getNextGaussian(); // getRandomNumber();
      chiSquareNumber += gaussianNumber * gaussianNumber;
    }

    return chiSquareNumber;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new ChiSquareDistribution(newRandomizer, degreeOfFreedom);
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
   * Set Degree of Freedom.
   * 
   * @param degreeOfFreedom
   *          of Freedom the new Degree of Freedom
   */
  public final void setDegreeOfFreedom(int degreeOfFreedom) {
    if (degreeOfFreedom > 0) {
      this.degreeOfFreedom = degreeOfFreedom;
    }
  }
}
