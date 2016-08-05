package org.jamesii.core.math.simpletree.unary;

import org.jamesii.core.math.simpletree.DoubleNode;

public class ExpNode extends AbstractUnaryNode {

  protected ExpNode(DoubleNode sub) {
    super(sub);
  }

  @Override
  protected double performOperation(double val) {
    return Math.exp(val);
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode sub) {
    return new ExpNode(sub);
  }

}
