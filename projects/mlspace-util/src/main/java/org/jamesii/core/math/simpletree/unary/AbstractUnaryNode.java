/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree.unary;

import java.util.Collections;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.DoubleNodes;
import org.jamesii.core.math.simpletree.UndefinedVariableException;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public abstract class AbstractUnaryNode implements DoubleNode {

  private final DoubleNode sub;

  protected AbstractUnaryNode(DoubleNode sub) {
    this.sub = sub;
  }

  @Override
  public double calculateValue(Map<String, ? extends Number> environment) {
    double val = sub.calculateValue(environment);
    return performOperation(val);
  }

  protected abstract double performOperation(double val);

  /**
   * Get new instance of node doing the same thing with the given sub-node, or a
   * simpler version of the equivalent calculation. Used by {@link #simplify()},
   * which should return a node with sub-trees/nodes whose value can be
   * calculated right away replaced with this value directly without modifying
   * the original node tree.
   *
   * @param sub
   * @return
   */
  protected abstract DoubleNode simplerCopy(DoubleNode sub);

  /**
   * @return the sub-node (for simplification of more complex expressions)
   */
  public final DoubleNode getSub() {
    return sub;
  }

  @Override
  public DoubleNode simplify() {
    try {
      double val = sub.calculateValue(Collections.<String, Number> emptyMap());
      return new FixedValueNode(performOperation(val));
    } catch (UndefinedVariableException e) {
      DoubleNode simpleSub = sub.simplify();
      return simplerCopy(simpleSub);
    }
  }

  @Override
  public String toString() {
    if (DoubleNodes.strictlyLowerPriority(sub, this)) {
      return "(" + sub + ")";
    } else {
      return sub.toString();
    }
  }

}
