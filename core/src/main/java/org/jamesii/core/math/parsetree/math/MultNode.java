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
 * Node that represents the multiplication operation.
 * 
 * @author Jan Himmelspach
 */
public class MultNode extends BinaryNode {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 7971913877802903524L;

  /**
   * Instantiates a new mult node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public MultNode(INode left, INode right) {
    super(left, right);
  }

  @Override
  public INode calc(ValueNode<?> l, ValueNode<?> r) {

    if ((l.isDouble() || r.isDouble())) {
      return new ValueNode<>(((Number) l.getValue()).doubleValue()
          * ((Number) r.getValue()).doubleValue());
    }

    if ((l.isLong() || r.isLong())) {
      return new ValueNode<>(((Number) l.getValue()).longValue()
          * ((Number) r.getValue()).longValue());
    }

    return new ValueNode<>((Integer) l.getValue() * (Integer) r.getValue());
  }

  @Override
  public String getName() {
    return "*";
  }

}
