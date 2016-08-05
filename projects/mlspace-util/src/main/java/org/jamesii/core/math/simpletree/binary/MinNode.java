package org.jamesii.core.math.simpletree.binary;

import org.jamesii.core.math.simpletree.DoubleNode;

/**
 * @author Arne Bittig
 * @date 21.08.2014
 */
public class MinNode extends AbstractBinaryNode {

  /**
   * @param leftSub
   * @param rightSub
   */
  public MinNode(DoubleNode leftSub, DoubleNode rightSub) {
    super(leftSub, rightSub);
  }

  @Override
  protected double performOperation(double left, double right) {
    return Math.min(left, right);
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode left, DoubleNode right) {
    return new MinNode(left, right);
  }

  @Override
  protected String opToString() {
    return "min(";
  }
}
