/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.AbstractCondition;
import org.jamesii.model.carules.CARule;
import org.jamesii.model.carules.ICACondition;

/**
 * Basic {@link ICACondition} implementation that can be used in either
 * preCondition or currentCondition of {@link CARule} and does simply combine a
 * number of other {@link ICACondition} using the OR operator.
 * 
 * @author Stefan Rybacki
 * 
 */
public class OrExpression extends AbstractCondition {

  /**
   * Instantiates a new or expression. And already adds the specified
   * {@link ICACondition} as first element in list of conditions to combine
   * using OR operator.
   * 
   * @param c
   *          the first condition to be added to number of {@link ICACondition}s
   */
  public OrExpression(ICACondition c) {
    addCondition(c);
  }

  @Override
  public boolean isTrue(int currentState, INeighborStates<Integer> neighbors) {
    boolean result = false;
    for (int i = 0; i < getConditionCount(); i++) {
      ICACondition c = getCondition(i);
      result |= c.isTrue(currentState, neighbors);
      if (result) {
        break;
      }
    }
    return result;
  }

  @Override
  public void addCondition(ICACondition c) {
    super.addCondition(c);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < getConditionCount(); i++) {
      ICACondition c = getCondition(i);
      builder.append(" OR ");
      builder.append(c);
    }

    if (getConditionCount() > 0) {
      builder.delete(0, " OR ".length());
    }

    if (getConditionCount() > 1) {
      return "(" + builder.toString() + ")";
    }

    return builder.toString();
  }

}
