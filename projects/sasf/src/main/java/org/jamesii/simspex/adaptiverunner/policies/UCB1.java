/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;

/**
 * Heuristic policy for multi-armed bandit problem. Implementation is based on
 * the algorithm presented by Auer et al in
 * "Finite-Time Analysis of the Multiarmed Bandit Problem" (2002).
 * 
 * @author Roland Ewald
 */
public class UCB1 extends UCB {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7537139949414032525L;

  /**
   * Calculates index: avg_i + (((2 ln n) / n_i))^(1/2)
   * 
   * @param arm
   * @return
   */
  @Override
  public double rankArm(int arm) {
    int pulls = getPullCount(arm);
    return getRewardSum(arm) / pulls
        - Math.sqrt(2 * Math.log(getOverallPullCount()) / pulls);
  }
  
  @Override
  public IMinBanditPolicy getNewInitializedCopy() {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

}
