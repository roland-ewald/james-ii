package org.jamesii.core.math.simpletree.binary;

import org.jamesii.core.math.simpletree.DoubleNode;

/**
 * @author Arne Bittig
 * @date 21.08.2014
 */
public class MaxNode extends AbstractBinaryNode {

  /**
   * @param leftSub
   * @param rightSub
   */
  public MaxNode(DoubleNode leftSub, DoubleNode rightSub) {
    super(leftSub, rightSub);
  }

  @Override
  protected double performOperation(double left, double right) {
    return Math.max(left, right);
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode left, DoubleNode right) {
    return new MaxNode(left, right);
  }

  @Override
  protected String opToString() {
    return "max(";
  }
}
