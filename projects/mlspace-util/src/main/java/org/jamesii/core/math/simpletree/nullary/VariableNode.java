/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree.nullary;

import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.UndefinedVariableException;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public final class VariableNode implements DoubleNode {

  private final String varName;

  public VariableNode(String varName) {
    this.varName = varName;
  }

  /**
   * @return the varName
   */
  public String getVarName() {
    return varName;
  }

  @Override
  public double calculateValue(Map<String, ? extends Number> environment) {
    Number value = environment.get(varName);
    if (value == null) {
      throw new UndefinedVariableException(varName, environment);
    }
    return value.doubleValue();
  }

  @Override
  public DoubleNode simplify() {
    return this;
  }

  @Override
  public String toString() {
    return varName;
  }
}
