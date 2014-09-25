/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.CARule;
import org.jamesii.model.carules.ICACondition;

/**
 * Implementation of {@link ICACondition} to be used in the currentCondition
 * part of {@link CARule}. It allows to specify a list of states that are
 * allowed to be the current state so that the related {@link CARule} can still
 * fire.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class CurrentStateCondition implements ICACondition {

  /**
   * The states that are allowed.
   */
  private final List<Integer> states = new ArrayList<>();

  /**
   * Instantiates a new current state condition.
   */
  public CurrentStateCondition() {
  }

  /**
   * Instantiates a new current state condition using the specified allowed
   * states as current state.
   * 
   * @param states
   *          the allowed states for current state
   */
  public CurrentStateCondition(int... states) {
    this();
    for (int s : states) {
      addState(s);
    }
  }

  @Override
  public ICACondition getCondition(int index) {
    return null;
  }

  @Override
  public int getConditionCount() {
    return 0;
  }

  @Override
  public boolean isTrue(int currentState, INeighborStates<Integer> neighbors) {
    for (Integer i : states) {
      if (i.intValue() == currentState) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds a state to the list of allowed current states.
   * 
   * @param state
   *          the state to add
   */
  public void addState(int state) {
    states.add(state);
  }

  /**
   * Gets the state list.
   * 
   * @return the state list
   */
  public List<Integer> getStateList() {
    return states;
  }

}
