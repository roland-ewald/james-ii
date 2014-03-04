/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * As long as no pull is finished, a random arm is pulled. After that, the best
 * arm is pulled with a probability of (1-prob) where prob=epsilon/(number of
 * finished pulls). With a probability of prob a random arm is pulled. See:
 * "Multi-Armed Bandit Algorithms and Empirical Evaluation" by Joann`es Vermorel
 * and Mehryar Mohri.
 * 
 * 
 * @author Rene Schulz
 * 
 */

public class EpsilonDecreasing extends SemiUniform {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8654028699005291456L;

  /**
   * The default epsilon (as used in paper by Vermorel and Mohri, see class
   * documentation).
   */
  static final int DEFAULT_EPSILON = 5;

  /**
   * Instantiates a new epsilon decreasing policy.
   */
  public EpsilonDecreasing() {
    setEpsilon(DEFAULT_EPSILON);
  }

  @Override
  public int nextChoice() {

    int choice;
    // if no pull is finished, pull random arm
    if ((getOverallPullCount() == 0)
        || (getRandom().nextDouble() < getProbability())) {
      choice = getRandomIndex();
    } else {
      choice = getBestIndex();
    }

    changed(choice);
    return choice;
  }

  /**
   * Get probability in case pull count > 0.
   * 
   * @return probability
   */
  protected double getProbability() {
    return getEpsilon() / getOverallPullCount();
  }

}
