/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree.unary;

import java.util.Collections;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public final class MinusNode extends AbstractUnaryNode {

  public MinusNode(DoubleNode sub) {
    super(sub);
  }

  @Override
  protected double performOperation(double val) {
    return -val;
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode sub) {
    if (sub instanceof FixedValueNode
        && sub.calculateValue(Collections.EMPTY_MAP) == 0) {
      return sub;
    }
    return new MinusNode(sub);
  }

  @Override
  public String toString() {
    return "-" + super.toString();
  }
}