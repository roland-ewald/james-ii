package org.jamesii.core.math.simpletree.mixed;

import java.util.Collections;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.LogicalNode;
import org.jamesii.core.math.simpletree.UndefinedVariableException;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;

/**
 *
 * @author Arne Bittig
 * @date 28.07.2014
 */
public class IfThenElseNode implements DoubleNode {

  private final LogicalNode condition;

  private final DoubleNode thenNode;

  private final DoubleNode elseNode;

  /**
   * @param condition
   *          condition expression to evaluate
   * @param thenNode
   *          expression whose value to return if condition is true
   * @param elseNode
   *          expression whose value to return if condition is false (may be
   *          null: 0 is returned then)
   */
  public IfThenElseNode(LogicalNode condition, DoubleNode thenNode,
      DoubleNode elseNode) {
    this.condition = condition;
    this.thenNode = thenNode;
    this.elseNode = elseNode;
  }

  @Override
  public double calculateValue(Map<String, ? extends Number> environment) {
    if (condition.calculateValue(environment)) {
      return thenNode.calculateValue(environment);
    } else if (elseNode == null) {
      return 0.;
    } else {
      return elseNode.calculateValue(environment);
    }
  }

  @Override
  public DoubleNode simplify() {
    try {
      if (condition.calculateValue(Collections.<String, Number> emptyMap())) {
        return thenNode.simplify();
      } else if (elseNode == null) {
        return new FixedValueNode(0.);
      } else {
        return elseNode.simplify();
      }
    } catch (UndefinedVariableException e) {
      return new IfThenElseNode(condition, // TODO: adapt if cond simplifiable
          thenNode.simplify(), elseNode == null ? null : elseNode.simplify());
    }
  }

  @Override
  public String toString() {
    return "if " + condition + " then " + thenNode
        + (elseNode == null ? "" : (" else " + elseNode));
  }
}
