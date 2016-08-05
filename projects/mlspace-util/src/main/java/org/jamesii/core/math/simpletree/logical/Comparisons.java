package org.jamesii.core.math.simpletree.logical;

import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.LogicalNode;

/**
 * @author Arne Bittig
 * @date 01.08.2014
 */
public final class Comparisons {

  private Comparisons() {
  }

  private abstract static class BinaryNode implements LogicalNode {

    private final DoubleNode left;

    private final DoubleNode right;

    BinaryNode(DoubleNode left, DoubleNode right) {
      this.left = left;
      this.right = right;
    }

    DoubleNode getLeft() {
      return left;
    }

    DoubleNode getRight() {
      return right;
    }

    @Override
    public String toString() {
      return left + compStr() + right;
    }

    protected abstract String compStr();
  }

  public static LogicalNode newFromCompString(DoubleNode left,
      String comparison, DoubleNode right) {
    switch (comparison) {
    case "==":
      return new Equals(left, right);
    case "<":
      return new LessThan(left, right);
    case "<=":
      return new LessOrEqual(left, right);
    case "!=":
      return new NotNode(new Equals(left, right));
    case ">":
      return new LessThan(right, left);
    case ">=":
      return new LessOrEqual(right, left);
    default:
      throw new IllegalArgumentException("Unknown comparison operation "
          + comparison);
    }
  }

  public static class LessThan extends BinaryNode {

    public LessThan(DoubleNode left, DoubleNode right) {
      super(left, right);
    }

    @Override
    public boolean calculateValue(Map<String, ? extends Number> variables) {
      return getLeft().calculateValue(variables) < getRight().calculateValue(
          variables);
    }

    @Override
    protected String compStr() {
      return "<";
    }

  }

  public static class LessOrEqual extends BinaryNode {

    public LessOrEqual(DoubleNode left, DoubleNode right) {
      super(left, right);
    }

    @Override
    public boolean calculateValue(Map<String, ? extends Number> variables) {
      return getLeft().calculateValue(variables) <= getRight().calculateValue(
          variables);
    }

    @Override
    protected String compStr() {
      return "<=";
    }
  }

  public static class Equals extends BinaryNode {

    public Equals(DoubleNode left, DoubleNode right) {
      super(left, right);
    }

    @Override
    public boolean calculateValue(Map<String, ? extends Number> variables) {
      return getLeft().calculateValue(variables) == getRight().calculateValue(
          variables);
    }

    @Override
    protected String compStr() {
      return "==";
    }
  }

  public static class EqualsWithTolerance extends BinaryNode {

    private final double tol;

    public EqualsWithTolerance(DoubleNode left, DoubleNode right, double tol) {
      super(left, right);
      this.tol = tol;
    }

    @Override
    public boolean calculateValue(Map<String, ? extends Number> variables) {
      return Math.abs(getLeft().calculateValue(variables)
          - getRight().calculateValue(variables)) <= tol;
    }

    @Override
    protected String compStr() {
      return "~";
    }
  }

}
