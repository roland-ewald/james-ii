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
public final class DivNode extends AbstractBinaryNode {

  public DivNode(DoubleNode leftSub, DoubleNode rightSub) {
    super(leftSub, rightSub);
  }

  @Override
  protected double performOperation(double left, double right) {
    return left / right;
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode left, DoubleNode right) {
    if (left instanceof FixedValueNode) {
      double value = left.calculateValue(Collections.EMPTY_MAP);
      if (value == 0.) {
        return left;
      }
      if (value == 1.) {
        if (right instanceof InverseNode) {
          return ((InverseNode) right).getSub();
        }
        return new InverseNode(right);
      }
      if (value == -1. && right instanceof InverseNode) {
        return new MinusNode(((AbstractUnaryNode) right).getSub()).simplify();
      }
    }

    return new DivNode(left, right);
  }

  @Override
  protected String opToString() {
    return "/";
  }

}
