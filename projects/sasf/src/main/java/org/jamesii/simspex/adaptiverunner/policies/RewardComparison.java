/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * Arms were chosen according to a SoftMax relationship based on the preference
 * of each arm. The preference raises, if the arm gets a smaller reward than the
 * current ReferenceReward (and the other way around). The ReferenceReward is
 * calculated based on the received rewards of all arms. See: Sutton/Barto:
 * "Reinforcement Learning: An Introduction", 1998. (it's called
 * "Reinforcement Comparison" there)
 * 
 * The method getNextChoice is taken from SoftMax.
 * 
 * @see SoftMax
 * 
 * @author Rene Schulz
 * 
 */
public class RewardComparison extends SoftMax {

  /** Serialisation ID. */
  private static final long serialVersionUID = 9031906098998098140L;

  /** The default value of alpha. */
  static final double DEFAULT_VAL_ALPHA = 0.5;

  /** The default value of beta. */
  static final double DEFAULT_VAL_BETA = 0.5;

  /** Reference-Reward. */
  private double refReward = 0.0;

  /**
   * Exploitation-Parameter (the higher, the more exploitation takes place). Has
   * to be > 0.0.
   */
  private double beta = DEFAULT_VAL_BETA;

  /** Parameter of the heuristic. Has to be in (0,1]. */
  private double alpha = DEFAULT_VAL_ALPHA;

  /** Array that stores the preferences for each arm. */
  private double[] preferences;

  @Override
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);
    preferences = new double[numberOfArms];
    // Initialization of the preferences for each arm.
    for (int i = 0; i < getNumOfArms(); i++) {
      preferences[i] = 0.0;
    }
  }

  /**
   * Calculates the probabilities for each arm and stores them in probs and
   * calculates sumProbs. Arms in quarantine get a probability of zero. Arms
   * which weren't pulled yet get a probability of 1.
   */
  @Override
  protected void calcProbs() {
    sumProbs = 0.0;
    // Calculated probability for each arm.
    for (int i = 0; i < getNumOfArms(); i++) {
      if (isQuarantined(i)) {
        probs[i] = 0.0;
      } else {
        probs[i] = Math.exp(preferences[i]);
      }
      // Add new probability to sumProbs.
      sumProbs += probs[i];
    }
    if (sumProbs == 0.0) {
      throw new IllegalStateException(
          "Probabilities weren't calculated correctly "
              + "(sumProbs=0.0; all arms in quarantine?)!");
    }
  }

  @Override
  public void receiveReward(int arm, double reward) {
    // ignore arms in quarantine.
    if (isQuarantined(arm)) {
      return;
    }
    super.receiveReward(arm, reward);
    // Determine the Reference-Reward at the first pull.
    if (getOverallPullCount() == 0) {
      refReward = reward;
    }
    // The smaller the higher the preference for the next rounds.
    preferences[arm] += beta * (refReward - reward);
    // Update the Reference-Reward.
    refReward += alpha * (reward - refReward);
  }

  public double getBeta() {
    return beta;
  }

  /**
   * Has to be > 0.0, other values will be ignored.
   * 
   * @param beta
   *          of the heuristic.
   */
  public void setBeta(double beta) {
    if (beta > 0.0) {
      this.beta = beta;
    }
  }

  public double getAlpha() {
    return alpha;
  }

  /**
   * Has to be in (0,1], other values will be ignred.
   * 
   * @param alpha
   */
  public void setAlpha(double alpha) {
    if ((alpha > 0) && (alpha <= 1.0)) {
      this.alpha = alpha;
    }
  }

  /**
   * This class doesn't use the parameter temperature: currentTemperature,
   * getTemperature and setTemperature will throw an exception.
   */
  @Override
  protected double currentTemperature() {
    throw new IllegalAccessError("This method is obsolete here!");
  }

  @Override
  public Double getTemperature() {
    throw new IllegalAccessError("This method is obsolete here!");
  }

  @Override
  public void setTemperature(Double temperature) {
    throw new IllegalAccessError("This method is obsolete here!");
  }
}
