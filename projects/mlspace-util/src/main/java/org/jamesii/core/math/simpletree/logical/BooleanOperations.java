package org.jamesii.core.math.simpletree.logical;

import java.util.Map;

import org.jamesii.core.math.simpletree.LogicalNode;

/**
 *
 * @author Arne Bittig
 * @date 01.08.2014
 */
public final class BooleanOperations {

  private BooleanOperations() {
  }

  private abstract static class BinaryOperation implements LogicalNode {

    private final LogicalNode left;

    private final LogicalNode right;

    BinaryOperation(LogicalNode left, LogicalNode right) {
      this.left = left;
      this.right = right;
    }

    LogicalNode getLeft() {
      return left;
    }

    LogicalNode getRight() {
      return right;
    }
  }

  public static class And extends BinaryOperation {

    public And(LogicalNode left, LogicalNode right) {
      super(left, right);
    }

    @Override
    public boolean calculateValue(Map<String, ? extends Number> variables) {
      return getLeft().calculateValue(variables)
          && getRight().calculateValue(variables);
    }
  }

  public static class Or extends BinaryOperation {

    public Or(LogicalNode left, LogicalNode right) {
      super(left, right);
    }

    @Override
    public boolean calculateValue(Map<String, ? extends Number> variables) {
      return getLeft().calculateValue(variables)
          || getRight().calculateValue(variables);
    }
  }

  public static class Xor extends BinaryOperation {

    public Xor(LogicalNode left, LogicalNode right) {
      super(left, right);
    }

    @Override
    public boolean calculateValue(Map<String, ? extends Number> variables) {
      return getLeft().calculateValue(variables)
          ^ getRight().calculateValue(variables);
    }
  }

  public static class Equals extends BinaryOperation {

    public Equals(LogicalNode left, LogicalNode right) {
      super(left, right);
    }

    @Override
    public boolean calculateValue(Map<String, ? extends Number> variables) {
      return getLeft().calculateValue(variables) == getRight().calculateValue(
          variables);
    }
  }

}
