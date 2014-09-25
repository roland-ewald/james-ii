/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.CARule;
import org.jamesii.model.carules.ICACondition;

/**
 * Implements a condition that can be used in the preCondition part of a
 * {@link CARule} and checks whether a given state does at least occurs min
 * times and at most max times in the neighborhood of the current cell.
 * 
 * @author Stefan Rybacki
 * 
 */
public class StateCondition implements ICACondition {

  /**
   * The max occurrence.
   */
  private int max;

  /**
   * Gets the max.
   * 
   * @return the max
   */
  public int getMax() {
    return max;
  }

  /**
   * Gets the min.
   * 
   * @return the min
   */
  public int getMin() {
    return min;
  }

  /**
   * Gets the state.
   * 
   * @return the state
   */
  public int getState() {
    return state;
  }

  /**
   * The min occurrence.
   */
  private int min;

  /**
   * The state to check for occurrence.
   */
  private int state;

  /**
   * Instantiates a new state condition.
   * 
   * @param state
   *          the state to check for in neighborhood
   * @param min
   *          the min allowed occurrence
   * @param max
   *          the max allowed occurrence
   */
  public StateCondition(int state, int min, int max) {
    this.state = state;
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean isTrue(int currentState, INeighborStates<Integer> neighbors) {
    int count = neighbors.getCountOf(state);
    return count >= min && count <= max;
  }

  @Override
  public String toString() {
    return state + "[" + min + ":" + max + "]";
  }

  @Override
  public int getConditionCount() {
    return 0;
  }

  @Override
  public ICACondition getCondition(int index) {
    return null;
  }

}
