/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree.binary;

import java.util.Collections;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.DoubleNodes;
import org.jamesii.core.math.simpletree.UndefinedVariableException;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;

public abstract class AbstractBinaryNode implements DoubleNode {

  private final DoubleNode leftSub;

  private final DoubleNode rightSub;

  protected AbstractBinaryNode(DoubleNode leftSub, DoubleNode rightSub) {
    this.leftSub = leftSub;
    this.rightSub = rightSub;
  }

  @Override
  public double calculateValue(Map<String, ? extends Number> environment) {
    double left = leftSub.calculateValue(environment);
    double right = rightSub.calculateValue(environment);
    return performOperation(left, right);
  }

  /**
   * Perform the operation associated with this binary node type
   *
   * @param left
   *          Left operand
   * @param right
   *          Right operand
   * @return Result
   */
  protected abstract double performOperation(double left, double right);

  @Override
  public DoubleNode simplify() {
    try {
      double leftVal =
          leftSub.calculateValue(Collections.<String, Number> emptyMap());
      double rightVal =
          rightSub.calculateValue(Collections.<String, Number> emptyMap());
      return new FixedValueNode(performOperation(leftVal, rightVal));
    } catch (UndefinedVariableException e) {
      DoubleNode simpleLeftSub = leftSub.simplify();
      DoubleNode simpleRightSub = rightSub.simplify();
      return simplerCopy(simpleLeftSub, simpleRightSub);
    }
  }

  /**
   * Create instance of same class with given left and right sub nodes (usually
   * simplifications of the existing ones), or equivalent simplification of the
   * calculation, if possible
   *
   * @param left
   *          Left sub node
   * @param right
   *          Right sub node
   * @return new TreeNode subclass with given sub nodes
   */
  protected abstract DoubleNode simplerCopy(DoubleNode left, DoubleNode right);

  protected abstract String opToString();

  @Override
  public String toString() {
    String opStr = opToString();
    if (opStr.endsWith("(")) {
      // binary function
      return opStr + leftSub + ',' + rightSub + ')';
    } // else binary operation with opStr = operand symbol
    StringBuilder sb = new StringBuilder();
    if (DoubleNodes.strictlyLowerPriority(leftSub, this)) {
      sb.append('(');
      sb.append(leftSub);
      sb.append(')');
    } else {
      sb.append(leftSub);
    }
    sb.append(opStr);
    if (DoubleNodes.lowerPrioritySecond(rightSub, this)) {
      sb.append('(');
      sb.append(rightSub);
      sb.append(')');
    } else {
      sb.append(rightSub);
    }
    return sb.toString();
  }
}