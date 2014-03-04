/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

import java.util.HashMap;
import java.util.Map;

/**
 * This policy might be beneficial for algorithm performance comparisons. It
 * proportionally selects arms with less reward more often than arms with more.
 * Before, an initialisation phase is done.
 * 
 * @author Roland Ewald
 * 
 */
public class BiasedRandomSelection extends RandomSelection {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6222786366980909553L;

  /**
   * Default number of initial passes through the set of arms. See
   * {@link BiasedRandomSelection#numInitPhase}.
   */
  private static final int DEFAULT_NUM_INITIAL_ROUNDS = 2;

  /** Default power of the bias. See {@link BiasedRandomSelection#biasPower}. */
  private static final int DEFAULT_BIAS_POWER = 3;

  /**
   * The number of initialisation phases. Each arm will be pulled at least this
   * number of times in the beginning (round-robin).
   */
  private int numInitPhase = DEFAULT_NUM_INITIAL_ROUNDS;

  /** The arm count for the initial phase. */
  private int initPhaseArm = 0;

  /** The bias power n, probability is then (1/x^n). */
  private double biasPower = DEFAULT_BIAS_POWER;

  @Override
  public int nextChoice() {
    int choice = isInitPhase() ? chooseForInitialPhase() : getRandomIndex();
    changed(choice);
    return choice;
  }

  /**
   * Implements simple initial phase.
   * 
   * @return arm in round-robin manner, or random arm
   */
  public int chooseForInitialPhase() {
    if (initPhaseArm < getNumOfArms()) {
      initPhaseArm++;
    } else {
      if (numInitPhase == 0) {
        leaveInitPhase();
      } else {
        numInitPhase--;
        initPhaseArm = 1;
      }
    }
    return isInitPhase() ? initPhaseArm - 1 : getRandomIndex();
  }

  /**
   * Selects a random arm.
   * 
   * @return index of the selected arm
   */
  @Override
  protected int getRandomIndex() {

    int countOKArms = 0;
    Map<Integer, Integer> indexMap = new HashMap<>();
    for (int i = 0; i < getNumOfArms(); i++) {
      if (isQuarantined(i) || getPullCount(i) == 0) {
        continue;
      }
      indexMap.put(countOKArms, i);
      countOKArms++;
    }
    if (countOKArms == 0) {
      return super.getRandomIndex();
    }

    double[] armValues = estimateArmValue(countOKArms, indexMap);

    // Choose at random
    double choice = getRandom().nextDouble();
    double sum = 0;
    for (int i = 0; i < countOKArms; i++) {
      if (sum <= choice && choice < sum + armValues[i]) {
        return indexMap.get(i);
      }
      sum += armValues[i];
    }
    return indexMap.get(countOKArms - 1);
  }

  /**
   * Estimate arm value. Calculates figure of merit (pulls per reward [1/x]).
   * 
   * @param numEligibleArms
   *          the number of eligible arms (can be chosen)
   * @param indexMap
   *          the index map to the eligible arms
   * @return the array of estimated value
   */
  private double[] estimateArmValue(int numEligibleArms,
      Map<Integer, Integer> indexMap) {
    double[] armRewardPull = new double[numEligibleArms];
    double avgRewardPullSum = 0;
    for (int i = 0; i < numEligibleArms; i++) {
      int armIndex = indexMap.get(i);
      double x = getPullCount(armIndex) / getRewardSum(armIndex);
      armRewardPull[i] = Math.pow(x, biasPower);
      avgRewardPullSum += armRewardPull[i];
    }

    // Normalise
    for (int i = 0; i < numEligibleArms; i++) {
      armRewardPull[i] /= avgRewardPullSum;
    }
    return armRewardPull;
  }

  /**
   * Gets the bias power.
   * 
   * @return the biasPower
   */
  public double getBiasPower() {
    return biasPower;
  }

  /**
   * Sets the bias power.
   * 
   * @param biasPower
   *          the biasPower to set
   */
  public void setBiasPower(double biasPower) {
    this.biasPower = biasPower;
  }

  public int getNumInitPhase() {
    return numInitPhase;
  }

  public void setNumInitPhase(int numInitPhase) {
    this.numInitPhase = numInitPhase;
  }

}
