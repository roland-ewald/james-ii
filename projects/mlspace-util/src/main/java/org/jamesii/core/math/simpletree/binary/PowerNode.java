/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree.binary;

import java.util.Collections;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;
import org.jamesii.core.math.simpletree.unary.InverseNode;
import org.jamesii.core.math.simpletree.unary.SquareNode;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public final class PowerNode extends AbstractBinaryNode {

  public PowerNode(DoubleNode leftSub, DoubleNode rightSub) {
    super(leftSub, rightSub);
  }

  @Override
  protected double performOperation(double left, double right) {
    return Math.pow(left, right);
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode left, DoubleNode right) {
    if (right instanceof FixedValueNode) {
      double value = right.calculateValue(Collections.EMPTY_MAP);
      if (value == 0.) {
        return new FixedValueNode(1.);
      }
      if (value == 1.) {
        return left;
      }
      if (value == -1.) {
        return new InverseNode(left);
      }
      if (value == 2.) {
        return new SquareNode(left);
      }
    }
    if (left instanceof FixedValueNode
        && Math.abs(left.calculateValue(Collections.EMPTY_MAP)) == 1.) {
      return left;
    }
    return new PowerNode(left, right);
  }

  @Override
  protected String opToString() {
    return "^";
  }

}
