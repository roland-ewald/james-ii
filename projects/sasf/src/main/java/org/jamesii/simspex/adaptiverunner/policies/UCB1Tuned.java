/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * Heuristic policy for multi-armed bandit problem. Implementation is based on
 * the algorithm presented by Auer et al in
 * "Finite-Time Analysis of the Multiarmed Bandit Problem" (2002).
 * 
 * @author Roland Ewald
 * 
 */
public class UCB1Tuned extends UCB1 {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4706705790968759653L;

  /** Sum of squared reward for each arm. */
  private double[] squaredRewardSums;

  @Override
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);
    squaredRewardSums = new double[numberOfArms];
  }

  @Override
  public void receiveReward(int arm, double reward) {
    super.receiveReward(arm, reward);
    squaredRewardSums[arm] += (reward * reward);
  }

  /**
   * Calculates index with some more elaborate ranking method than UCB1.
   * 
   * @param arm
   * @return
   */
  @Override
  public double rankArm(int arm) {
    int pulls = getPullCount(arm);
    return getRewardSum(arm)
        / pulls
        - Math.sqrt((Math.log(getOverallPullCount()) / pulls)
            * Math.min(0.25, v(arm)));
  }

  protected double v(int arm) {
    int pulls = getPullCount(arm);
    double avg = getRewardSum(arm) / pulls;
    return (squaredRewardSums[arm] / pulls) - (avg * avg)
        + Math.sqrt((2 * Math.log(getOverallPullCount())) / pulls);
  }
}
