package org.jamesii.core.math.match;

import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;

/**
 * @author Arne Bittig
 * @date 07.08.2014
 */
public final class RangeMatches {
  private RangeMatches() {
  }

  private static abstract class RangeMatch implements ValueMatch {

    private final DoubleNode node;

    protected RangeMatch(DoubleNode node) {
      this.node = node.simplify();
    }

    @Override
    public boolean matches(Object value, Map<String, ?> variables) {
      return compare(((Number) value).doubleValue(),
          node.calculateValue((Map<String, ? extends Number>) variables));
    }

    protected abstract boolean compare(double value, double compareVal);

    @Override
    public String toString() {
      return opString() + node.toString();
    }

    protected abstract String opString();
  }

  public static class Equals extends RangeMatch {

    /**
     * @param node
     */
    public Equals(DoubleNode node) {
      super(node);
    }

    @Override
    protected boolean compare(double value, double compareVal) {
      return value == compareVal;
    }

    @Override
    protected String opString() {
      return "==";
    }
  }

  public static class LessThan extends RangeMatch {

    public LessThan(DoubleNode node) {
      super(node);
    }

    @Override
    protected boolean compare(double value, double compareVal) {
      return value < compareVal;
    }

    @Override
    protected String opString() {
      return "<";
    }
  }

  public static class GreaterThan extends RangeMatch {
    /**
     * @param node
     */
    public GreaterThan(DoubleNode node) {
      super(node);
    }

    @Override
    protected boolean compare(double value, double compareVal) {
      return value > compareVal;
    }

    @Override
    protected String opString() {
      return ">";
    }
  }

  public static class LessOrEqual extends RangeMatch {

    /**
     * @param node
     */
    public LessOrEqual(DoubleNode node) {
      super(node);
    }

    @Override
    protected boolean compare(double value, double compareVal) {
      return value <= compareVal;
    }

    @Override
    protected String opString() {
      return "<=";
    }
  }

  public static class GreaterOrEqual extends RangeMatch {

    /**
     * @param node
     */
    public GreaterOrEqual(DoubleNode node) {
      super(node);
    }

    @Override
    protected boolean compare(double value, double compareVal) {
      return value >= compareVal;
    }

    @Override
    protected String opString() {
      return ">=";
    }
  }

  /**
   * Factory method for matching an interval
   *
   * @param lower
   *          Lower bound
   * @param incLower
   *          Flag whether to include lower boundary
   * @param upper
   *          Upper bound
   * @param incUpper
   *          Flag whether to include upper boundary
   * @return Match method for specified interval
   */
  public static ValueMatch newInterval(DoubleNode lower, boolean incLower,
      DoubleNode upper, boolean incUpper) {
    ValueMatch low =
        incLower ? new GreaterOrEqual(lower) : new GreaterThan(lower);
    ValueMatch up = incUpper ? new LessOrEqual(upper) : new LessThan(upper);
    return new And(low, up);
  }

  static class And implements ValueMatch {

    private final ValueMatch match1;

    private final ValueMatch match2;

    /**
     * @param match1
     * @param match2
     */
    And(ValueMatch match1, ValueMatch match2) {
      this.match1 = match1;
      this.match2 = match2;
    }

    @Override
    public boolean matches(Object value, Map<String, ?> variables) {
      return match1.matches(value, variables)
          && match2.matches(value, variables);
    }

    private String defaultString() {
      return match1.toString() + " && " + match2.toString();
    }

    @Override
    public String toString() {
      if (!(match1 instanceof GreaterOrEqual || match1 instanceof GreaterThan)
          || !(match2 instanceof LessOrEqual || match2 instanceof LessThan)) {
        return defaultString();
      }
      StringBuilder str = new StringBuilder(" in ");
      if (match1 instanceof GreaterThan) {
        str.append('(');
      } else {
        str.append('[');
      }
      str.append(((RangeMatch) match1).node);
      str.append(',');
      str.append(((RangeMatch) match2).node);
      if (match2 instanceof LessThan) {
        str.append(')');
      } else {
        str.append(']');
      }
      return str.toString();
    }
  }
}