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
import org.jamesii.core.math.simpletree.unary.AbstractUnaryNode;
import org.jamesii.core.math.simpletree.unary.MinusNode;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public final class AddNode extends AbstractBinaryNode {

  public AddNode(DoubleNode leftSub, DoubleNode rightSub) {
    super(leftSub, rightSub);
  }

  @Override
  protected double performOperation(double left, double right) {
    return left + right;
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode left, DoubleNode right) {
    if (right instanceof FixedValueNode
        && right.calculateValue(Collections.EMPTY_MAP) == 0) {
      return left;
    }
    if (left instanceof FixedValueNode
        && left.calculateValue(Collections.EMPTY_MAP) == 0) {
      return right;
    }
    if (right instanceof MinusNode) {
      return new SubtractNode(left, ((AbstractUnaryNode) right).getSub());
    }
    if (left instanceof MinusNode) {
      return new SubtractNode(right, ((AbstractUnaryNode) left).getSub());
    }
    return new AddNode(left, right);
  }

  @Override
  protected String opToString() {
    return "+";
  }
}
