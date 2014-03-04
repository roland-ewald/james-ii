/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies.plugintype;


import java.util.List;

import org.jamesii.core.base.IEntity;

/**
 * Interface for gambling policies that try to *minimize* the reward gained by
 * pulling arms from a multi-armed bandit machine.
 * 
 * @author Roland Ewald
 * 
 */
public interface IMinBanditPolicy extends IEntity {

  /**
   * Initialises policy.
   * 
   * @param numOfArms
   *          the number of arms
   * @param horizon
   *          the horizon, i.e. the number of pulls
   */
  void init(int numOfArms, int horizon);

  /**
   * Retrieves the next choice of the algorithm.
   * 
   * @return the number of the arm to be pulled
   */
  int nextChoice();

  /**
   * This method is called when the policy receives reward for pulling a
   * specific arm.
   * 
   * @param arm
   * @param reward
   */
  void receiveReward(int arm, double reward);
  
  /**
   * Gets the number of arms.
   * 
   * @return the number of arms
   */
  int getNumOfArms();
  
  /**
   * Signals the policy to exclude a given arm from future selection decisions.
   * This might be necessary if one arm turns out to be non-functional, i.e. it
   * leads to failures etc.
   * 
   * @param optionIndex
   */
  void quarantine(int optionIndex);

  /**
   * Returns a list with all options that had to be quarantined.
   * 
   * @return a list with all options that had to be quarantined
   */
  List<Integer> getQuarantinedOptions();
}
