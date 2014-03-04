/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

import java.util.ArrayList;

/**
 * Super class for UCB policies. See
 * "Finite-Time Analysis of the Multiarmed Bandit Problem" (Auer et al., 2002).
 * It takes care of the normalisation, as the UCB policies work on probability
 * distributions with support in [0,1]. To do so, *all* rewards that are
 * received via
 * {@link org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy#receiveReward(int, double)}
 * are stored, and in case a new maximum is encountered all reward sums in
 * {@link AbstractMinBanditPolicy#rewardSums} will be re-calculated.
 * 
 * @see org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy
 * 
 * @author Roland Ewald
 * 
 */
public abstract class UCB extends AbstractMinBanditPolicy {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4761650237730390581L;

  /** Counts the number of arms already pulled in the initialisation phase. */
  private int initPhaseArm = 0;

  /** List containing the rewards received per arm. */
  private DoubleArrayList[] rewards;

  /** Current maximum reward that has been received so far. */
  private double maxReward = Double.MIN_VALUE;

  @Override
  public int nextChoice() {
    int choice = isInitPhase() ? nextChoiceInitPhase() : selectBestArm();
    changed(choice);
    return choice;
  }

  /**
   * Choose arm when being in the initial phase.
   * 
   * @return
   */
  protected int nextChoiceInitPhase() {
    // Rotate between arms as long as no reward has been received
    if (initPhaseArm < getNumOfArms()) {
      initPhaseArm++;
    } else if (getOverallPullCount() == 0) {
      initPhaseArm = 1;
    } else {
      leaveInitPhase();
      return selectBestArm();
    }
    return initPhaseArm - 1;
  }

  /**
   * Returns best arm by considering all arms for which rewards have been
   * received, calls {@link UCB1#rankArm(int)} and selects algorithm with
   * minimal ranking.
   * 
   * @return
   */
  protected int selectBestArm() {

    int bestArm = -1;
    double minRanking = Double.MAX_VALUE;

    for (int i = 0; i < getNumOfArms(); i++) {

      // Ignore arms that have not been pulled yet
      // (or where getting the reward takes too long)
      if (getPullCount(i) == 0) {
        continue;
      }

      // If arm has been quarantined, ignore it
      if (isQuarantined(i)) {
        continue;
      }

      // Calculate ranking
      double ranking = rankArm(i);

      if (ranking < minRanking) {
        minRanking = ranking;
        bestArm = i;
      }
    }

    return bestArm;
  }

  @Override
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);
    rewards = new DoubleArrayList[numberOfArms];
    for (int i = 0; i < rewards.length; i++) {
      rewards[i] = new DoubleArrayList();
    }
  }

  @Override
  public void receiveReward(int arm, double originalReward) {
    double reward = checkConsistence(arm, originalReward);
    // If newly received reward is too high, start normalisation
    if (maxReward < reward) {
      if (maxReward > 0) {
        // Normalise the rewards of each arm
        double normFactor = maxReward / reward;
        for (int i = 0; i < rewards.length; i++) {
          double rewardSum = 0.0;
          DoubleArrayList rewardList = rewards[i];
          for (int j = 0; j < rewardList.size(); j++) {
            double newReward = rewardList.get(j) * normFactor;
            rewardSum += newReward;
            rewardList.set(j, newReward);
          }
          setRewardSum(i, rewardSum);
        }
      }
      // Then add new maximum
      maxReward = reward;
      receiveNormReward(arm, 1.0);
    } else {
      receiveNormReward(arm, reward / maxReward);
    }
  }

  /**
   * Stores normalised reward.
   * 
   * @param arm
   *          the arm with which the reward shall be associated
   * @param reward
   *          the normalised reward
   */
  protected void receiveNormReward(int arm, double reward) {
    rewards[arm].add(reward);
    super.receiveReward(arm, reward * maxReward);
  }

  /**
   * Ranks the arm. The arm with the minimal ranking will be chosen.
   * 
   * @param arm
   *          index of the arm
   * @return ranking of the arm
   */
  protected abstract double rankArm(int arm);

}

/**
 * Simple dummy class to trick the compiler into accepting arrays of generic
 * classes.
 * 
 * @author Roland Ewald
 */
class DoubleArrayList extends ArrayList<Double> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5392908479846101331L;

}
