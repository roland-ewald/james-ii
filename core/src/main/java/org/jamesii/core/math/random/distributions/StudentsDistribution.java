/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * This class helps to generate random numbers
 * 
 * which are distributed according to Student's t-distribution.
 * 
 * By default the degree of freedom is 3!
 * 
 * @author tf081
 */
public class StudentsDistribution extends AbstractDistribution {

  /** Serialization ID. */
  private static final long serialVersionUID = -3894284008341019364L;

  /** Degree of Freedom. */
  private int degreeOfFreedom = 3;

  /** NormalDistribution helps to create a Student's t-distribution. */
  private AbstractNormalDistribution normDist;

  /** ChiSquareDistribution helps to create a Student's t-distribution. */
  private ChiSquareDistribution chisquareDist;

  /**
   * Instantiates a new students distribution.
   * 
   * @param seed
   *          the seed
   */
  public StudentsDistribution(long seed) {
    super(seed);
    normDist = new NormalDistribution(seed);
    chisquareDist = new ChiSquareDistribution(seed);
  }

  /**
   * The Constructor.
   * 
   * @param randomizer
   *          the randomizer
   */
  public StudentsDistribution(IRandom randomizer) {
    super(randomizer);
    normDist = new NormalDistribution(randomizer);
    chisquareDist = new ChiSquareDistribution(randomizer);
  }

  /**
   * Standard constructor.
   * 
   * @param randomizer
   *          random number generator
   * @param degOfFreedom
   *          Degree of Freedom for the Student's t-distribution
   */
  public StudentsDistribution(IRandom randomizer, int degOfFreedom) {
    super(randomizer);
    normDist = new NormalDistribution(randomizer);
    chisquareDist = new ChiSquareDistribution(randomizer, degOfFreedom);
    setDegreeOfFreedom(degOfFreedom);
  }

  /**
   * Extended constructor also allowing customization of the used gaussian (i.e.
   * normal) distribution
   * 
   * @param randomizer
   *          random number generator
   * @param degOfFreedom
   *          Degree of Freedom for the Student's t-distribution
   */
  public StudentsDistribution(IRandom randomizer, int degOfFreedom,
      AbstractNormalDistribution normDist) {
    super(randomizer);
    this.normDist = normDist;
    chisquareDist =
        new ChiSquareDistribution(randomizer, degOfFreedom, normDist);
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

    // Student's t random number
    double studNumber;

    // Chi-Square random number
    double chiSquareNumber;

    // Gaussian random number
    double gaussianNumber;

    gaussianNumber = normDist.getNextGaussian(); // getRandomNumber();
    chiSquareNumber = chisquareDist.getRandomNumber();
    studNumber =
        gaussianNumber * StrictMath.sqrt(degreeOfFreedom / chiSquareNumber);

    return studNumber;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new StudentsDistribution(newRandomizer, degreeOfFreedom);
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
   *          the degree of freedom
   */
  public final void setDegreeOfFreedom(int degreeOfFreedom) {
    if (degreeOfFreedom > 0) {
      this.degreeOfFreedom = degreeOfFreedom;
    }
  }
}
