/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * PoissonDistribution -
 * 
 * Class for generating Poisson distributed random numbers with mean lambda.
 * NOTE: Generating Poisson random numbers is very expensive (just look at the
 * code), so use it carefully. Better implementations are welcome.
 * 
 * Creation date: 09.03.2007
 * 
 * @author Matthias Jeschke
 */
public class PoissonDistribution extends AbstractDistribution {

  /** Serial version UID. */
  private static final long serialVersionUID = -3289472123466420000L;

  /** Mean of distribution. */
  private double lambda = 0.0;

  /**
   * Coefficient constant
   */
  private static final double COF[] = { 76.18009172947146, -86.50532032941677,
      24.01409824083091, -1.231739572450155, 0.1208650973866179e-2,
      -0.5395239384953e-5 };

  /**
   * Constructor.
   * 
   * @param seed
   *          the seed
   */
  public PoissonDistribution(long seed) {
    super(seed);
  }

  /**
   * Constructor.
   * 
   * @param randomizer
   *          the randomizer
   */
  public PoissonDistribution(IRandom randomizer) {
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
  public PoissonDistribution(IRandom randomizer, double lambda) {
    super(randomizer);
    this.lambda = lambda;
  }

  /**
   * Natural log of gamma function.
   * 
   * @param x
   *          x-value
   * 
   * @return log of gamma function at x
   */
  private double gammaLog(double x) {
    double y = x;
    double tmp = x + 5.5;
    double ser = 1.000000000190015;

    tmp -= (x + 0.5) * Math.log(tmp);
    for (int j = 0; j <= 5; j++) {
      ser += COF[j] / ++y;
    }

    return -tmp + Math.log(2.5066282746310005 * ser / x);
  }

  /**
   * Gets the lambda.
   * 
   * @return the lambda
   */
  public double getLambda() {
    return this.lambda;
  }

  // ab358: moved from local var in #getRandomNumber() to private field, as
  // oldm made little sense in there (and produced a "dead store" sonar
  // violation)
  private double oldm = -1.0f;

  // ab358: moved from local var in #getRandomNumber() along with previous
  private double g = 0.0f;

  private double sq = 0.0f;

  private double alxm = 0.0f;

  @Override
  /**
   * Generate random number
   * 
   * @return generated random number
   */
  public final double getRandomNumber() {
    double em;
    double t;
    double y;

    // simple method with small lambda
    if (lambda < 12.0) {
      if (lambda != oldm) {
        oldm = lambda;
        // only recalculate g if lambda has changed
        g = Math.exp(-lambda);
      }
      em = -1;
      t = 1.0f;
      do {
        ++em;
        t *= getRandom().nextDouble();
      } while (t > g);
    }
    // rejection (envelope) method
    else {
      if (lambda != oldm) {
        oldm = lambda;
        sq = Math.sqrt(2.0 * lambda);
        alxm = Math.log(lambda);
        g = lambda * alxm - gammaLog(lambda + 1.0);
      }
      do {
        do {
          y = Math.tan(Math.PI * getRandom().nextDouble());
          em = sq * y + lambda;
        } while (em < 0.0);
        em = Math.floor(em);
        t = 0.9 * (1.0 + y * y) * Math.exp(em * alxm - gammaLog(em + 1.0) - g);
      } while (getRandom().nextDouble() > t);
    }

    return em;
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
