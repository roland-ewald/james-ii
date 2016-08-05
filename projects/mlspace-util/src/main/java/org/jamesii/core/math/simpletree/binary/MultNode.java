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
import org.jamesii.core.math.simpletree.unary.InverseNode;
import org.jamesii.core.math.simpletree.unary.MinusNode;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public final class MultNode extends AbstractBinaryNode {

  public MultNode(DoubleNode leftSub, DoubleNode rightSub) {
    super(leftSub, rightSub);
  }

  @Override
  protected double performOperation(double left, double right) {
    return left * right;
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode left, DoubleNode right) {
    if (right instanceof FixedValueNode) {
      double value = right.calculateValue(Collections.EMPTY_MAP);
      if (value == 0.) {
        return right;
      }
      if (value == 1.) {
        return left;
      }
      if (value == -1.) {
        return new MinusNode(left);
      }
    }
    if (left instanceof FixedValueNode) {
      double value = left.calculateValue(Collections.EMPTY_MAP);
      if (value == 0.) {
        return left;
      }
      if (value == 1.) {
        return right;
      }
      if (value == -1.) {
        return new MinusNode(right);
      }
    }
    if (right instanceof InverseNode) {
      if (left instanceof InverseNode) {
        return new InverseNode(new MultNode(
            ((AbstractUnaryNode) left).getSub(),
            ((AbstractUnaryNode) right).getSub()));
      }
      return new DivNode(left, ((AbstractUnaryNode) right).getSub());
    }
    if (left instanceof InverseNode) {
      return new DivNode(right, ((AbstractUnaryNode) left).getSub());
    }
    return new MultNode(left, right);
  }

  @Override
  protected String opToString() {
    return "*";
  }
}
