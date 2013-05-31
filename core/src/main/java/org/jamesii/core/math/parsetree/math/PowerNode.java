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
 * Node that represents the power operation.
 * 
 * @author Oliver Röwer
 */
public class PowerNode extends BinaryNode {

  private static final long serialVersionUID = 4489864337234998703L;

  /**
   * Instantiates a new power node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public PowerNode(INode left, INode right) {
    super(left, right);
  }

  @Override
  public INode calc(ValueNode<?> l, ValueNode<?> r) {

    return new ValueNode<>(Math.pow(((Number) l.getValue()).doubleValue(),
        ((Number) r.getValue()).doubleValue()));
  }

  @Override
  public String getName() {
    return "^";
  }
}
