/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import org.jamesii.model.cacore.INeighborStates;

/**
 * The interface for conditions that can be used to define custom rules in
 * {@link CARule}.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface ICACondition {

  /**
   * Gets sub conditions
   * 
   * @return the condition count
   */
  int getConditionCount();

  /**
   * Gets the specified sub condition.
   * 
   * @param index
   *          the index of the sub condition
   * 
   * @return the sub condition
   */
  ICACondition getCondition(int index);

  /**
   * Checks if condition is {@code true} for the given parameters.
   * 
   * @param currentState
   *          the current state
   * @param neighbors
   *          the neighbor states
   * 
   * @return true, if condition is met using given parameters
   */
  boolean isTrue(int currentState, INeighborStates<Integer> neighbors);
}
