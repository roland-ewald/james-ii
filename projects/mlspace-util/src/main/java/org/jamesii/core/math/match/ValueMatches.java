package org.jamesii.core.math.match;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.UndefinedVariableException;
import org.jamesii.core.math.simpletree.nullary.VariableNode;

/**
 * @author Arne Bittig
 * @date 07.08.2014
 */
public final class ValueMatches {
  private ValueMatches() {
  }

  public static ValueMatch newEquals(DoubleNode node) {
    if (node instanceof VariableNode) {
      return new EqualsVariableValue(((VariableNode) node).getVarName());
    }
    try {
      return new EqualsValue(node.calculateValue(Collections.EMPTY_MAP));
    } catch (UndefinedVariableException ex) {
      return new RangeMatches.Equals(node);
    }
  }

  public static ValueMatch newEqualsString(String str) {
    return new EqualsValue(str);
  }

  public static class EqualsValue implements ValueMatch {

    private final Object eqVal;

    /**
     * @param eqVal
     */
    public EqualsValue(Object eqVal) {
      this.eqVal = eqVal;
    }

    @Override
    public boolean matches(Object value, Map<String, ?> variables) {
      return eqVal.equals(value);
    }

    @Override
    public String toString() {
      return "==" + eqVal;
    }
  }

  public static class EqualsVariableValue implements ValueMatch {

    private final String varName;

    /**
     * @param eqVal
     */
    public EqualsVariableValue(String varName) {
      this.varName = varName;
    }

    @Override
    public boolean matches(Object value, Map<String, ?> variables) {
      return value.equals(variables.get(varName));
    }

    @Override
    public String toString() {
      return "==" + varName;
    }
  }

  /**
   * @author Arne Bittig
   * @date 08.08.2014
   */
  public static class AnyOf implements ValueMatch {

    private final Collection<?> coll;

    /**
     * @param coll
     */
    public AnyOf(Collection<?> coll) {
      this.coll = coll;
    }

    @Override
    public boolean matches(Object value, Map<String, ?> variables) {
      return coll.contains(value);
    }

    @Override
    public String toString() {
      return " in " + coll;
    }

  }
}
