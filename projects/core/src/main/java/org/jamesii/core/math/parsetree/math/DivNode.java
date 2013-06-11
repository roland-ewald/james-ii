/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;

/**
 * Node that represents the division operation.
 * 
 * @author Jan Himmelspach
 * 
 */
public class DivNode extends BinaryNode {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 4435699270314520383L;

  /**
   * Instantiates a new div node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public DivNode(INode left, INode right) {
    super(left, right);
  }

  @Override
  public INode calc(ValueNode<?> l, ValueNode<?> r) {

    boolean d = l.isDouble() || r.isDouble();

    if (d) {
      return new ValueNode<>(((Number) l.getValue()).doubleValue()
          / ((Number) r.getValue()).doubleValue());
    }

    return new ValueNode<>((Integer) l.getValue() / (Integer) r.getValue());
  }

  @Override
  public String getName() {
    return "/";
  }

}
