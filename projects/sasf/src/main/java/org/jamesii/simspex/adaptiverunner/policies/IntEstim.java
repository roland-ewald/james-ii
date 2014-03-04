/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.core.math.statistics.univariate.ErrorFunction;
import org.jamesii.core.math.statistics.univariate.StandardDeviation;

/**
 * First Version of IntEstim strategy. When receiving a reward, standard
 * deviation and arithmetical mean for the arm are calculated. The best arm
 * (depending on standard deviation and arithmetical mean)is pulled each round.
 * See: "Multi-Armed Bandit Algorithms and Empirical Evaluation" by Joann`es
 * Vermorel and Mehryar Mohri. Initialization: In a initial phase each arm is
 * pulled once. For the second pull, the standard deviation of each arm is set
 * to first reward * startDeviation/100. Additional changes: After pulling an
 * arm, the standard deviation of this arm is set to 0. That should optimize
 * parallel work.
 * 
 * @author Rene Schulz
 * 
 */

public class IntEstim extends RandomSelection {

  /** Serialisation ID. */
  private static final long serialVersionUID = -9083756624223606500L;

  /** Counts number of arms that are already initialized. */
  private int initPhaseArm = 0;

  /**
   * Parameter for the start of the heuristic has to be in [0,100]. The higher,
   * the more worse arms will be tested.
   */
  private int startDeviation = 20;

  /**
   * Parameter of the heuristic. Has to be in (0,1). Note that smaller alpha
   * values lead to more exploration.
   */
  private double alpha = 0.05;

  /** List containing the rewards received per arm. */
  private DoubleArrayList[] rewards;

  /** List containing the empirical mean for each arm. */
  private double[] empMean;

  /** List containing the empirical standard deviation. */
  private double[] empDeviation;

  @Override
  // Initialization of the array for the rewards, means and deviations.
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);
    empMean = new double[numberOfArms];
    empDeviation = new double[numberOfArms];
    rewards = new DoubleArrayList[numberOfArms];
    for (int i = 0; i < rewards.length; i++) {
      rewards[i] = new DoubleArrayList();
    }
  }

  @Override
  public void receiveReward(int arm, double reward) {
    // If arm has been quarantined, ignore it
    if (isQuarantined(arm)) {
      return;
    }
    super.receiveReward(arm, reward);
    // Add new reward to the referring arm.
    rewards[arm].add(reward);
    // Calculate empMean and empDeviation for the specific arm.
    DoubleArrayList rewardList = rewards[arm];
    empMean[arm] = ArithmeticMean.arithmeticMean(rewardList);
    // After the first pull, the standard deviation
    // is set to 'startDeviation' % of first reward.
    if (getPullCount(arm) == 1) {
      empDeviation[arm] = getRewardSum(arm) * startDeviation / 100;
    } else {
      empDeviation[arm] = StandardDeviation.standardDeviation(rewardList);
    }
  }

  @Override
  public int nextChoice() {
    int choice = 0;
    // In the initialization Phase each arm is pulled once.
    if (initPhaseArm < getNumOfArms()) {
      choice = initPhaseArm;
      initPhaseArm++;
    } else {
      // If no pull is finished yet pull random arm
      if (getOverallPullCount() == 0) {
        choice = getRandomIndex();
      } else {
        // The approximation of the inverted error function may not be exact!
        double c =
            Math.sqrt(2) * ErrorFunction.inverf(1 - 2 * getCurrentAlpha());
        double minReward = Double.MAX_VALUE;
        double currentReward;
        for (int arm = 0; arm < getNumOfArms(); arm++) {
          // If arm has been quarantined, ignore it.
          if (isQuarantined(arm)) {
            continue;
          }
          int pulls = getPullCount(arm);
          // If arm has no pull finished yet, it's too slow to be chosen.
          if (pulls == 0) {
            currentReward = Double.MAX_VALUE;
          } else {
            currentReward =
                empMean[arm] - (empDeviation[arm] / Math.sqrt(pulls)) * c;
          }
          if (currentReward < minReward) {
            minReward = currentReward;
            choice = arm;
          }
        }
      }
    }
    // The algorithm shall investigate an arm with acceptable mean and high
    // deviation further. But before the pull is finished and the policy receive
    // the reward, the arm might be chosen again. So a bad arm might be
    // pulled several times just because of it's deviation and the parallel
    // working. To fix this, the deviation of a pulled arm is set to 0.
    empDeviation[choice] = 0.0;
    changed(choice);
    return choice;
  }

  public int getStartDeviation() {
    return startDeviation;
  }

  public void setStartDeviation(int startDeviation) {
    if ((startDeviation >= 0) && (startDeviation <= 100)) {
      this.startDeviation = startDeviation;
    }
  }

  /**
   * Gets the alpha parameter.
   * 
   * @return the alpha
   */
  protected double getAlpha() {
    return alpha;
  }

  /**
   * Set the exploration parameter called alpha. Has to be in (0,1), other vales
   * will be ignored.
   * 
   * @param alpha
   *          has to be in (0,1).
   */
  protected void setAlpha(double alpha) {
    if ((0 < alpha) && (alpha < 1)) {
      this.alpha = alpha;
    }
  }

  /**
   * Returns the current value of the alpha parameter.
   * 
   * @return the current value of alpha.
   */
  protected double getCurrentAlpha() {
    return alpha;
  }

  /**
   * Gets the number of arms that are already initialized.
   * 
   * @return the number of arms that are already initialized
   */
  protected int getInitPhaseArm() {
    return initPhaseArm;
  }

}
