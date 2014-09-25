/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple abstract implementation of {@link ICACondition}.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractCondition implements ICACondition {

  /**
   * The conditions list.
   */
  private final List<ICACondition> conditions = new ArrayList<>();

  @Override
  public int getConditionCount() {
    return conditions.size();
  }

  @Override
  public ICACondition getCondition(int index) {
    return conditions.get(index);
  }

  /**
   * Adds a condition to the list of conditions.
   * 
   * @param condition
   *          the condition
   */
  protected void addCondition(ICACondition condition) {
    if (condition == null) {
      return;
    }

    if (!conditions.contains(condition)) {
      conditions.add(condition);
    }
  }

  /**
   * Removes a condition from the list of conditios.
   * 
   * @param condition
   *          the condition
   */
  protected void removeCondition(ICACondition condition) {
    conditions.remove(condition);
  }
}
