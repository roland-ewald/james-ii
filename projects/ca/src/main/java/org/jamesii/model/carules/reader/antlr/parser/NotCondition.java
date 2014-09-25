/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.ICACondition;

/**
 * Simple inverting implementation of {@link ICACondition} which takes another
 * {@link ICACondition} and inverts its {@link #isTrue(int, INeighborStates)}
 * method.
 * 
 * @author Stefan Rybacki
 * 
 */
public class NotCondition implements ICACondition {

  /**
   * The condition to invert.
   */
  private ICACondition condition;

  /**
   * Gets the condition.
   * 
   * @return the condition
   */
  public ICACondition getCondition() {
    return condition;
  }

  /**
   * Instantiates a new not condition for the specified condition.
   * 
   * @param condition
   *          the condition to invert
   */
  public NotCondition(ICACondition condition) {
    this.condition = condition;
  }

  @Override
  public boolean isTrue(int currentState, INeighborStates<Integer> neighbors) {
    if (condition == null) {
      return false;
    }
    return !condition.isTrue(currentState, neighbors);
  }

  @Override
  public String toString() {
    return "NOT " + condition.toString();
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
