/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * This implementation of the {@link EpsilonGreedyDecreasing} strategy has an
 * initialization phase in which each arm is used at least once. Comes in handy
 * for performance experiments.
 * 
 * @author Roland Ewald
 * 
 */
public class EpsilonGreedyDecrInit extends EpsilonGreedyDecreasing {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2158119561602383872L;

  /** Counts number of arms that are already initialised (see {@link UCB}). */
  private int initPhaseArm = 0;

  /**
   * The minimal number of trials required per option. Corresponds to the number
   * of initial rounds.
   */
  private int minNumOfTrials;

  /**
   * Instantiates a new epsilon-greedy decreasing policy with initialization.
   * 
   * @param minimalNumberOfTrials
   *          the minimal number of trials
   */
  public EpsilonGreedyDecrInit(int minimalNumberOfTrials) {
    minNumOfTrials = minimalNumberOfTrials;
  }

  @Override
  public int nextChoice() {
    int choice = getInitArmIndex();
    if (choice < 0) {
      return super.nextChoice();
    }
    changed(choice);
    return choice;
  }

  /**
   * Gets the the arm index for initialisation.
   * 
   * @return the arm index
   */
  protected int getInitArmIndex() {
    if (initPhaseArm >= getNumOfArms()) {
      minNumOfTrials--;
      if (minNumOfTrials <= 0) {
        return -1;
      } else {
        initPhaseArm = 0;
      }
    }
    return initPhaseArm++;
  }
}
