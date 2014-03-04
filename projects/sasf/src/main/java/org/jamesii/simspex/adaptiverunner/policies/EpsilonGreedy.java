/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * As long as no pull is finished, a random arm is pulled. After that, the best
 * arm is pulled with a probability of (1- epsilon). With a probability of
 * epsilon a random arm is pulled. See:
 * "Multi-Armed Bandit Algorithms and Empirical Evaluation" by Joann`es Vermorel
 * and Mehryar Mohri.
 * 
 * 
 * @author Rene Schulz
 * 
 */

public class EpsilonGreedy extends SemiUniform {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5796298576909035327L;

  @Override
  public int nextChoice() {
    int choice;
    // if no pull is finished, pull random arm; otherwise pull random arm with
    // probability of epsilon
    if ((getRandom().nextDouble() < getEpsilon())
        || (getOverallPullCount() == 0)) {
      choice = getRandomIndex();
    } else {
      choice = getBestIndex();
    }

    changed(choice);
    return choice;
  }

}
