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
 * Very basic implementation of {@link ICACondition} where the outcome of
 * {@link #isTrue(int, INeighborStates)} can be specified using the parameter in
 * the constructor.
 * 
 * @author Stefan Rybacki
 * 
 */
public class BooleanCondition implements ICACondition {

  /**
   * The condition {@link #isTrue(int, INeighborStates)} returns.
   */
  private boolean condition;

  /**
   * Instantiates a new boolean condition.
   * 
   * @param condition
   *          the condition that {@link #isTrue(int, INeighborStates)} is
   *          returning
   */
  public BooleanCondition(boolean condition) {
    this.condition = condition;
  }

  @Override
  public boolean isTrue(int currentState, INeighborStates<Integer> neighbors) {
    return condition;
  }

  @Override
  public String toString() {
    return Boolean.valueOf(condition).toString();
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
