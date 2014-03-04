/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * Abstract class for all semi-uniform strategies of the multi-armed bandit
 * problem. extends (@link RandomSelection) for using getRandomIndex().
 * 
 * Vermorel and Mohri (ECML 2005) define semi-uniform methods as follows (p. 3):
 * 
 * Methods that imply a binary distinction between exploitation (the greedy
 * choice) and exploration (uniform probability over a set of levers) are known
 * as semi-uniform methods.
 * 
 * 
 * @author Rene Schulz
 * 
 */
public abstract class SemiUniform extends RandomSelection {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3367200134710412624L;

  /**
   * The default value for epsilon (from the literature, see class
   * documentation).
   */
  public static final double DEFAULT_EPSILON = 0.15;

  /**
   * Parameter of the heuristic. Has to be >0. The algorithm's performance
   * relies very much on epsilon. The higher epsilon, the more exploration takes
   * place. For example 0.05, 0.10 or 0.15.
   */
  private double epsilon = DEFAULT_EPSILON;

  /**
   * Epsilon has to be > 0. Other value will be ignored.
   * 
   * @param epsilon
   *          the new value
   */
  public void setEpsilon(double epsilon) {
    if (epsilon > 0) {
      this.epsilon = epsilon;
    }
  }

  /**
   * Gets the epsilon.
   * 
   * @return the epsilon
   */
  public double getEpsilon() {
    return epsilon;
  }

  /**
   * Returns index of the arm with the currently best (= smallest) average
   * reward. Therefore at least one pull had to be finished.
   * 
   * @return index of the arm with the minimal average reward
   */
  protected int getBestIndex() {
    int arm = -1;
    double minAvg = Double.MAX_VALUE;
    for (int i = 0; i < getNumOfArms(); i++) {
      if (isQuarantined(i)) {
        continue;
      }
      double avg = getRewardSum(i) / getPullCount(i);
      if (avg < minAvg) {
        arm = i;
        minAvg = avg;
      }
    }
    // throw exception if no arm was selected
    if (arm == -1) {
      throw new IllegalStateException("getBestIndex(): can't "
          + "select any index (all arms in quarantine?)!");
    }
    return arm;
  }
}
