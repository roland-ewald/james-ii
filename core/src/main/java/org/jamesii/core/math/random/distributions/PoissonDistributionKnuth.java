package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * PoissonDistribution -
 * 
 * Class for generating Poisson distributed random numbers with mean lambda.
 * NOTE: This variant is based on the algorithm by Knuth.
 * 
 * Creation date: 11.09.2009
 * 
 * @author Matthias Jeschke
 */
public class PoissonDistributionKnuth extends AbstractDistribution {

  /** Serial version UID. */
  private static final long serialVersionUID = -7569597560413912270L;

  /** Mean of distribution. */
  private double lambda = 0.0;

  /**
   * Constructor.
   * 
   * @param seed
   *          the seed
   */
  public PoissonDistributionKnuth(long seed) {
    super(seed);
  }

  /**
   * Constructor.
   * 
   * @param randomizer
   *          the randomizer
   */
  public PoissonDistributionKnuth(IRandom randomizer) {
    super(randomizer);
  }

  /**
   * Constructor.
   * 
   * @param randomizer
   *          the randomizer
   * @param lambda
   *          the lambda
   */
  public PoissonDistributionKnuth(IRandom randomizer, double lambda) {
    super(randomizer);
    this.lambda = lambda;
  }

  /**
   * Gets the lambda.
   * 
   * @return the lambda
   */
  public double getLambda() {
    return this.lambda;
  }

  @Override
  public double getRandomNumber() {
    // initialization
    double l = Math.exp(-this.lambda);
    double k = 0.0;
    double p = 1.0;

    do {
      k = k + 1.0;
      p = p * getRandom().nextDouble();
    } while (p > l);

    return k - 1.0;
  }

  @Override
  /**
   * Gets a new distribution object that uses the same parameters and the same
   * randomizer object (needed to duplicate distributions without having to
   * create new randomizers) NOTE: if this method is used for initializing
   * random distributions for stuff which gets computed concurrently by several
   * threads (in one JVM) the repeatability of the random number is not
   * guaranteed and this a simulation run is not repeatable!
   * 
   * @return
   */
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new PoissonDistribution(newRandomizer, lambda);
  }

  /**
   * Sets the parameter lambda (mean).
   * 
   * @param lambda
   *          the lambda
   */
  public void setLambda(double lambda) {
    this.lambda = lambda;
  }
}
