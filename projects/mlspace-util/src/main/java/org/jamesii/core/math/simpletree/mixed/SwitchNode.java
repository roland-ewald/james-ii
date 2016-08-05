package org.jamesii.core.math.simpletree.mixed;

import java.util.Collections;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.LogicalNode;
import org.jamesii.core.math.simpletree.UndefinedVariableException;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;

/**
 * @author Arne Bittig
 * @date 21.01.2015
 */
public class SwitchNode implements DoubleNode {

  private final LogicalNode condition;

  public SwitchNode(LogicalNode condition) {
    this.condition = condition;
  }

  @Override
  public double calculateValue(Map<String, ? extends Number> environment) {
    if (condition.calculateValue(environment)) {
      return 1.;
    }
    return 0;
  }

  @Override
  public DoubleNode simplify() {
    try {
      boolean sw =
          condition.calculateValue(Collections.<String, Number> emptyMap());
      return new FixedValueNode(sw ? 1. : 0.);
    } catch (UndefinedVariableException e) {
      return this;
    }
  }

  @Override
  public String toString() {
    return "(" + condition + ")";
  }

}
