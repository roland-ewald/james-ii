package org.jamesii.core.math.simpletree.logical;

import java.util.Map;

import org.jamesii.core.math.simpletree.LogicalNode;
import org.jamesii.core.math.simpletree.logical.Comparisons.Equals;

/**
 * Opposite of a given logical expression
 *
 * @author Arne Bittig
 * @date 01.08.2014
 */
public class NotNode implements LogicalNode {

  private final LogicalNode condition;

  /**
   * @param condition
   *          Expression of which to calculate the opposite
   */
  public NotNode(LogicalNode condition) {
    this.condition = condition;
  }

  @Override
  public boolean calculateValue(Map<String, ? extends Number> variables) {
    return !condition.calculateValue(variables);
  }

  LogicalNode getNode() {
    return condition;
  }

  @Override
  public String toString() {
    if (condition instanceof Equals) {
      return condition.toString().replace("==", "!=");
    }
    return "NOT (" + condition + ")";
  }
}