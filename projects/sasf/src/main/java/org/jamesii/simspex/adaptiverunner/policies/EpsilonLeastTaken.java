/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * As long as no pull is finished, a random arm is pulled. After that, the least
 * taken (least SELECTED) arm is pulled with a probability of
 * prob=4*epsilon/(4+m*m) where m is the number of times the least-taken lever
 * has already been selected. With a probability of 1-prob the best arm is
 * pulled. See: "Multi-Armed Bandit Algorithms and Empirical Evaluation" by
 * Joann`es Vermorel and Mehryar Mohri (it's called LeastTaken there).
 * 
 * 
 * @author Rene Schulz
 * 
 */

public class EpsilonLeastTaken extends SemiUniform {

  /** Serialisation ID. */
  private static final long serialVersionUID = 285383524166746504L;

  /** Stores how often each arm was selected. */
  private int[] selections;

  @Override
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);
    selections = new int[getNumOfArms()];
  }

  @Override
  public int nextChoice() {
    // Index of the least taken arm.
    int i = getLeastTakenArm();
    // Probability to pull least taken arm.
    double prob = 4 * getEpsilon() / (4 + selections[i] * selections[i]);

    int choice;
    // If no pull is finished, pull random arm.
    // Otherwise pull best or least taken arm.
    if (getOverallPullCount() == 0) {
      choice = getRandomIndex();
    } else {
      if (getRandom().nextDouble() < prob) {
        choice = i;
      } else {
        choice = getBestIndex();
      }
    }
    changed(choice);
    selections[choice]++;
    return choice;
  }

  /**
   * Returns the least taken (least selected) arm.
   * 
   * @return the least taken arm.
   */
  protected int getLeastTakenArm() {
    int leastTakenArm = -1;
    double minSelections = Double.MAX_VALUE;

    for (int i = 0; i < getNumOfArms(); i++) {
      // If arm has been quarantined, ignore it
      if (isQuarantined(i)) {
        continue;
      }

      int numOfSelections = selections[i];
      if (numOfSelections < minSelections) {
        minSelections = numOfSelections;
        leastTakenArm = i;
      }
    }
    return leastTakenArm;
  }
}
