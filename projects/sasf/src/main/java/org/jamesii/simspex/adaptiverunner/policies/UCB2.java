/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * 
 * Heuristic policy for multi-armed bandit problem. Implementation is based on
 * the algorithm presented by Auer et al in
 * "Finite-Time Analysis of the Multiarmed Bandit Problem" (2002).
 * 
 * @author Roland Ewald
 * 
 */
public class UCB2 extends UCB {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1623835704848664912L;

  /** The default value of the alpha parameter. See {@link UCB2#alpha}. */
  private static final double DEFAULT_ALPHA_VALUE = 0.001;

  /**
   * Adjusts the trade-off between exploitation and exploration. Has to be in
   * (0,1).
   */
  private double alpha = DEFAULT_ALPHA_VALUE;

  /** Epoch count per arm. */
  private int[] epochs;

  /** Number of remaining pulls in the current epoch. */
  private int repeatCounter = 0;

  /** Currently chosen arm. */
  private int currentChoice = -1;

  @Override
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);
    epochs = new int[numberOfArms];
  }

  @Override
  public int nextChoice() {
    if (repeatCounter <= 0) {
      int choice = super.nextChoice();
      if (isInitPhase()) {
        return choice;
      }
      currentChoice = choice;
      repeatCounter = tau(epochs[choice] + 1) - tau(epochs[choice]);
    }
    repeatCounter--;
    return currentChoice;
  }

  @Override
  protected double rankArm(int arm) {
    double tauForArm = tau(epochs[arm]);
    // ((1+alpha)ln(en/t(r))/2t(r))^(1/2)
    return (getRewardSum(arm) / getPullCount(arm))
        - Math.sqrt((1 + alpha)
            * Math.log(Math.E * getOverallPullCount() / tauForArm)
            / (2 * tauForArm));
  }

  /**
   * Calculates tau function. t(r) = ceil((1+alpha)^r)
   * 
   * @param armEpochs
   *          number of epochs an arm was played
   * @return ceil((1+alpha)^r)
   */
  protected int tau(int armEpochs) {
    return (int) Math.ceil(Math.pow(1 + alpha, armEpochs));
  }

  public double getAlpha() {
    return alpha;
  }

  /**
   * Sets alpha, which has to be in (0,1). All other values will be ignored.
   * 
   * @param alpha
   *          the new alpha parameter
   */
  public void setAlpha(double alpha) {
    if (alpha > 0 && alpha < 1) {
      this.alpha = alpha;
    }
  }

}
