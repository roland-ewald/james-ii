/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree.nullary;

import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public final class FixedValueNode implements DoubleNode {

  private final double value;

  public FixedValueNode(double value) {
    this.value = value;
  }

  @Override
  public double calculateValue(Map<String, ? extends Number> environment) {
    return value;
  }

  @Override
  public DoubleNode simplify() {
    return this;
  }

  @Override
  public String toString() {
    return Double.toString(value);
  }
}
