/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * InverseGaussianDistribution -
 * 
 * This class helps to generate normal distributed random numbers.
 * 
 * @author Arne Bittig (based on NormalDistribution - @author Roland Ewald)
 */
public class InverseGaussianDistribution extends AbstractDistribution {

  /** Serialization ID. */
  private static final long serialVersionUID = -3003543767983550114L;

  /** Shape parameter. */
  private double lambda;

  /** Value mean. */
  private double mean;

  /** Number from N(0,1) used in IG number generation */
  private AbstractNormalDistribution normDist;

  /**
   * Instantiates a new normal distribution.
   * 
   * @param seed
   *          the seed
   */
  public InverseGaussianDistribution(long seed) {
    super(seed);
    this.mean = 1;
    this.lambda = 1;
    normDist = new NormalDistribution(seed);
  }

  /**
   * Instantiates a new normal distribution.
   * 
   * @param randomizer
   *          the randomizer
   */
  public InverseGaussianDistribution(IRandom randomizer) {
    super(randomizer);
    this.mean = 1;
    this.lambda = 1;
    normDist = new NormalDistribution(randomizer);
  }

  /**
   * Standard constructor.
   * 
   * @param randomizer
   *          random number generator
   * @param mu
   *          mean value
   * @param lambda
   *          shape parameter
   */
  public InverseGaussianDistribution(IRandom randomizer, double mu,
      double lambda) {
    super(randomizer);
    this.mean = mu;
    this.lambda = lambda;
    normDist = new NormalDistribution(randomizer);
  }

  /**
   * Extended constructor also allowing customization of the used gaussian
   * distribution
   * 
   * @param randomizer
   *          random number generator
   * @param mu
   *          mean value
   * @param lambda
   *          shape parameter
   * @param normDist
   *          normal distribution N(0,1)
   */
  public InverseGaussianDistribution(IRandom randomizer, double mu,
      double lambda, AbstractNormalDistribution normDist) {
    super(randomizer);
    this.mean = mu;
    this.lambda = lambda;
    this.normDist = normDist;
    this.normDist.setMean(0);
    this.normDist.setDeviation(1);
  }

  /**
   * Get lambda.
   * 
   * @return lambda
   */
  public double getLambda() {
    return lambda;
  }

  /**
   * Get mean.
   * 
   * @return mean
   */
  public double getMean() {
    return mean;
  }

  /**
   * General IG number generation for given mu and lambda
   * 
   * @param mu
   *          the mean
   * @param lambda
   *          the shape parameter
   * @return pseudo random number from IG(mu,lambda)
   */
  public double inverseGaussian(double mu, double lambda) {
    // sample from a normal distribution with a mean of 0 and 1 st. dev.
    double v = normDist.getNextGaussian();
    double y = v * v * mu / lambda;
    double x = (2 + y - Math.sqrt(y * (y + 4))) / 2;
    // sample from a uniform distribution between 0 and 1
    double test = getRandom().nextDouble();
    if (test * (x + 1.0) <= 1.0) {
      return mu * x;
    }
    return mu / x;
    // from
    // http://en.wikipedia.org/w/index.php?title=Inverse_Gaussian_distribution
    // &oldid=399965919
    // #Generating_random_variates_from_an_inverse-Gaussian_distribution
    // double y = v * v;
    // double x = mu + (mu * mu * y) / (2 * lambda_) - (mu / (2 * lambda_))
    // * Math.sqrt(4 * mu * lambda_ * y + mu * mu * y * y);
    // double test = randomizer.nextDouble();
    // if (test <= (mu) / (mu + x))
    // return x;
    // else
    // return (mu * mu) / x;
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
    return inverseGaussian(mean, lambda);
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new InverseGaussianDistribution(newRandomizer, mean, lambda,
        normDist);
  }

  /**
   * Set lambda.
   * 
   * @param lambda
   *          the lambda
   */
  public void setLambda(double lambda) {
    if (lambda > 0) {
      this.lambda = lambda;
      // normDist.setDeviation(Math.sqrt(mean / lambda));
    }
  }

  /**
   * Set mean.
   * 
   * @param mean
   *          the mean
   */
  public void setMean(double mean) {
    if (mean > 0) {
      this.mean = mean;
      // normDist.setDeviation(Math.sqrt(mean / lambda));
    }
  }

  @Override
  public String toString() {
    return "IG(" + mean + "," + lambda + ")";
  }
}
