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
public final class InverseNode extends AbstractUnaryNode {

  public InverseNode(DoubleNode sub) {
    super(sub);
  }

  @Override
  protected double performOperation(double val) {
    return 1. / val;
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode sub) {
    if (sub instanceof FixedValueNode
        && Math.abs(sub.calculateValue(Collections.EMPTY_MAP)) == 1.) {
      return sub;
    }
    return new InverseNode(sub);
  }

  @Override
  public String toString() {
    return "1./" + super.toString();
  }
}