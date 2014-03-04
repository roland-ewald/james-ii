/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * As long as no pull is finished, a random arm is pulled. After that, the best
 * arm is pulled with a probability of (1-prob) where
 * prob=(epsilon*log10(t)+1)/t with t=number of finished pulls. With a
 * probability of prob a random arm is pulled. See:
 * "Multi-Armed Bandit Algorithms and Empirical Evaluation" by Joann`es Vermorel
 * and Mehryar Mohri. (it's called GreedyMix there)
 * 
 * Our implementation differs from the paper in adding an additional "1" (see
 * concrete formula below). This prevents a probability of 0 in the 2nd round
 * (finishedPullsCount==1) and the probability is a little bit lower in the
 * first 10 rounds. Otherwise the exploration phase would be extremely short.
 * The asymptotical behaviour should not be affected much.
 * 
 * 
 * @author Rene Schulz
 * 
 */

public class EpsilonDecreasingMix extends EpsilonDecreasing {

  /** Serialisation ID. */
  private static final long serialVersionUID = -2957420273786701816L;

  /**
   * Instantiates a new epsilon decreasing mix policy.
   */
  public EpsilonDecreasingMix() {
    setEpsilon(DEFAULT_EPSILON);
  }

  @Override
  protected double getProbability() {
    return (getEpsilon() * Math.log10(getOverallPullCount()) + 1)
        / getOverallPullCount();
  }

}
