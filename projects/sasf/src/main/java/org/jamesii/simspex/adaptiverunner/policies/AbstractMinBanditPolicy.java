/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.Entity;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;


/**
 * Default abstract class for {@link IMinBanditPolicy} implementations. It is of
 * type {@link Entity} and hence can be observed. It notifies without any hint
 * that it was initialised, and with a pair (arm, reward) when it was notified
 * of an arm's performance. Sub-classes have to call
 * {@link Entity#changed(Object)} with the index of the selected arm.
 * 
 * @author Roland Ewald
 */
public abstract class AbstractMinBanditPolicy extends Entity implements
    IMinBanditPolicy {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2708610903610411328L;

  /** The current number of arms. */
  private int numOfArms;

  /** The (minimal) length of the horizon. */
  private int horizon;

  /** Stores sum of rewards per arm. */
  private double[] rewardSums;

  /**
   * Stores quarantine information for each arm. If flag for i-th arm is true,
   * the i-th arm cannot be used any more, as it has failed before.
   */
  private boolean[] quarantine;

  /** Stores the number of pulls per arm. */
  private int[] pulls;

  /**
   * Counts overall number of finished pulls (where the reward has already been
   * returned).
   */
  private int overallPullCount;

  /** Flag that determines whether the initialisation phase has ended or not. */
  private boolean initPhase = true;

  @Override
  public void init(int numberOfArms, int horizonSize) {
    numOfArms = numberOfArms;
    horizon = horizonSize;
    initPhase = true;
    rewardSums = new double[numberOfArms];
    pulls = new int[numberOfArms];
    quarantine = new boolean[numberOfArms];
    overallPullCount = 0;
    changed();
  }

  /**
   * Throws an exception when the arm-index is out of bounce or the reward has a
   * negative value. When arm is in quarantine, the function returns 0.0, else
   * the givien reward.
   * 
   * @param arm
   *          index to check.
   * @param reward
   *          to check.
   * @return 0.0 if arm is in quarantine. Otherwise the given reward.
   */
  public double checkConsistence(int arm, double reward) {
    // Some error handling
    if (arm < 0 || arm >= numOfArms) {
      throw new IllegalArgumentException("Arm with index " + arm
          + " is not defined!");
    }
    if (reward < 0) {
      throw new IllegalArgumentException(
          "Negative reward is not allowed, but reward for arm " + arm
              + " was set to " + reward);
    }
    if (quarantine[arm]) {
      return 0.0;
    }
    return reward;
  }

  @Override
  public void receiveReward(int arm, double reward) {
    checkConsistence(arm, reward);

    // Ignore arms in quarantine
    if (quarantine[arm]) {
      return;
    }

    // Fill auxiliary data structures
    rewardSums[arm] += reward;
    pulls[arm]++;
    overallPullCount++;
    changed(new Pair<>(arm, reward));
  }

  @Override
  public void quarantine(int optionIndex) {
    quarantine[optionIndex] = true;
  }

  @Override
  public List<Integer> getQuarantinedOptions() {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < quarantine.length; i++) {
      if (quarantine[i]) {
        result.add(i);
      }
    }
    return result;
  }

  /**
   * Gets the reward sum for a given arm.
   * 
   * @param armIndex
   *          the arm index
   * @return the reward sum
   */
  protected double getRewardSum(int armIndex) {
    return rewardSums[armIndex];
  }
  
  /**
   * Gets the pull count for a given arm.
   * 
   * @param armIndex
   *          the arm index
   * @return the pull count
   */
  protected int getPullCount(int armIndex) {
    return pulls[armIndex];
  }

  
  @Override
  public int getNumOfArms() {
    return numOfArms;
  }

  /**
   * Gets the horizon.
   * 
   * @return the horizon
   */
  protected int getHorizon() {
    return horizon;
  }

  /**
   * Sets the reward sum for a given arm.
   * 
   * @param armIndex
   *          the arm index
   * @param newSum
   *          the new sum
   */
  protected void setRewardSum(int armIndex, double newSum) {
    rewardSums[armIndex] = newSum;
  }

  /**
   * Checks if the policy is in its initialization phase.
   * 
   * @return true, if it is in initialization phase
   */
  protected boolean isInitPhase() {
    return initPhase;
  }

  /**
   * Signal end of initialization phase.
   */
  protected void leaveInitPhase() {
    this.initPhase = false;
  }

  /**
   * Checks if an arm is quarantined.
   * 
   * @param armIndex
   *          the arm index
   * @return true, if the arm is quarantined
   */
  protected boolean isQuarantined(int armIndex) {
    return quarantine[armIndex];
  }

  /**
   * Gets the overall pulls count.
   * 
   * @return the finished pulls count
   */
  protected int getOverallPullCount() {
    return overallPullCount;
  }
}
