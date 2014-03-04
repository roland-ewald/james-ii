/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;


/**
 * Based on (@Link SoftMax). Initially each arm gets the same probability
 * (1/number of arms). When receiving a reward, the probability of the currently
 * best arm (smallest average reward) is increased towards 1. The probability of
 * other pulled arms is decreased towards toward 0. (not pulled arms will be
 * ignored) See: Sutton/Barto: "Reinforcement Learning: An Introduction", 1998.
 * 
 * @author Rene Schulz
 * 
 */

public class Pursuit extends SoftMax {

  /** Serialisation ID. */
  private static final long serialVersionUID = -746601889363268562L;

  /**
   * The default value for parameter beta. Has to be in (0.0,1.0). 0.01 is given
   * in (Sutton & Barto p. 44, see above).
   */
  public static final double DEFAULT_VAL_BETA = 0.01;

  /**
   * Exploitation-Parameter (the higher, the more exploitation takes place). Has
   * to be in (0.0,1.0).
   */
  private double beta = DEFAULT_VAL_BETA;

  @Override
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);

    sumProbs = 1.0;
    for (int i = 0; i < getNumOfArms(); i++) {
      probs[i] = 1.0 / getNumOfArms();
    }
  }

  @Override
  protected void calcProbs() {
    // This method is obsolete here.
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
      double average = getRewardSum(i) / getPullCount(i);
      if (average < minAvg) {
        arm = i;
        minAvg = average;
      }
    }
    // throw exception if no arm was selected
    if (arm == -1) {
      throw new IllegalStateException("getBestIndex(): can't "
          + "select any index (all arms in quarantine?)!");
    }
    return arm;
  }

  @Override
  public void receiveReward(int arm, double reward) {
    // ignore arms in quarantine.
    if (isQuarantined(arm)) {
      return;
    }
    super.receiveReward(arm, reward);
    int bestArm = getBestIndex();
    sumProbs = 0.0;
    for (int i = 0; i < getNumOfArms(); i++) {
      // Change probabilities.
      if (i == bestArm) {
        probs[i] += beta * (1.0 - probs[i]);
      } else {
        if (getPullCount(i) > 0) {
          probs[i] += beta * (0.0 - probs[i]);
          // Avoid probabilities less than zero.
          if (probs[i] < 0.0) {
            probs[i] = 0.0;
          }
        }
      }
      sumProbs += probs[i];
    }
  }

  public double getBeta() {
    return beta;
  }

  /**
   * Has to be > 0.0 and < 1.0, other values will be ignored.
   * 
   * @param beta
   *          of the heuristic.
   */
  public void setBeta(double beta) {
    if ((beta > 0.0) && (beta < 1.0)) {
      this.beta = beta;
    }
  }
}
