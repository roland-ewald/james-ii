/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.BinomialCoefficient;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * The Class BinomialDistribution.
 * 
 * WARNING! Might produce incorrect results with a high number of trials.
 * 
 * @author Jan Himmelspach
 */
public class BinomialDistribution extends AbstractDistribution {

  /** Serialization ID. */
  private static final long serialVersionUID = -1167327899702116218L;

  /**
   * Binmonial/Bernoulli distribution.
   * 
   * @param x
   *          Number of "hits" of A
   * @param n
   *          Number of independent executions
   * @param p
   *          probability for A
   * 
   * @return the probability of z <= x occurences of A
   */
  public static double distribution(int n, int x, double p) {

    double result = 0;

    if (x >= 0) {
      for (int k = 0; k <= x; k++) {
        result += probability(n, k, p);
      }
    }
    return result;
  }

  /**
   * Mean of a binomial distribution (n * p).
   * 
   * @param n
   *          number of experiment executions
   * @param p
   *          probability for A
   * 
   * @return the double
   */
  public static double mean(int n, double p) {
    return n * p;
  }

  /**
   * Binmonial/Bernoulli distribution Returns the probability for x times A on n
   * experiment executions, if A has the probability p.
   * 
   * @param x
   *          Number of "hits" of A
   * @param n
   *          Number of independent executions
   * @param p
   *          probability for A
   * 
   * @return the double
   */
  public static double probability(int n, int x, double p) {
    return BinomialCoefficient.binomialQuick(n, x) * Math.pow(p, x)
        * Math.pow(1 - p, n - x);
  }

  /**
   * Variance of a binomial distribution.
   * 
   * @param n
   *          number of experiment executions
   * @param p
   *          probability for A
   * 
   * @return the double
   */
  public static double variance(int n, double p) {
    return n * p * (1 - p);
  }

  /** Hit probability. */
  private double hitProbability = 0.5;

  /** Number of trials. */
  private int numOfTrials = 1;

  /**
   * Constructor for factory.
   * 
   * @param seed
   *          the seed
   */
  public BinomialDistribution(long seed) {
    super(seed);
  }

  /**
   * Constructor for factory.
   * 
   * @param rand
   *          the rand
   */
  public BinomialDistribution(IRandom rand) {
    super(rand);
  }

  /**
   * Default constructor.
   * 
   * @param rand
   *          the rand
   * @param hitProb
   *          the hit prob
   * @param nTrials
   *          the n trials
   */
  public BinomialDistribution(IRandom rand, double hitProb, int nTrials) {
    super(rand);
    setHitProbability(hitProb);
    setNumOfTrials(nTrials);
  }

  /**
   * Gets the hit probability.
   * 
   * @return the hit probability
   */
  public double getHitProbability() {
    return hitProbability;
  }

  /**
   * Gets the num of trials.
   * 
   * @return the num of trials
   */
  public int getNumOfTrials() {
    return numOfTrials;
  }

  // Static functions

  @Override
  public double getRandomNumber() {
    if (this.numOfTrials < 50) {

      double u = this.getRandom().nextDouble();

      int randNumber = 0;

      double probabilitySum =
          probability(numOfTrials, randNumber, hitProbability);

      while (probabilitySum <= u) {
        randNumber++;
        probabilitySum += probability(numOfTrials, randNumber, hitProbability);
      }

      return randNumber;
    }
    return this.bu();
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new BinomialDistribution(newRandomizer, hitProbability, numOfTrials);
  }

  /**
   * Sets the hit probability.
   * 
   * @param hitProbability
   *          (has to be in [0,1])
   */
  public final void setHitProbability(double hitProbability) {
    if (hitProbability < 0 || hitProbability > 1) {
      throw new IllegalArgumentException(
          "Binomial Distribution: hit probability ('" + hitProbability
              + "') has to be in [0,1].");
    }
    this.hitProbability = hitProbability;
  }

  /**
   * Sets the number of trials.
   * 
   * @param numOfTrials
   *          (has to be positive)
   */
  public final void setNumOfTrials(int numOfTrials) {
    if (numOfTrials < 0) {
      throw new IllegalArgumentException(
          "Binomial Distribution: number of trials ('" + numOfTrials
              + "') has to be >= 0.");
    }
    this.numOfTrials = numOfTrials;
  }

  private double bu() {
    double x = 0;
    double k = 0;
    for (int i = 0; i < this.numOfTrials; i++) {
      double u = this.getRandom().nextDouble();
      k = k + 1;
      if (u <= this.hitProbability) {
        x++;
      }
    }
    return x;
  }

}
