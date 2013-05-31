/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import org.jamesii.SimSystem;
import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;

/**
 * Node that represents the addition operation.
 * 
 * @author Jan Himmelspach
 */
public class AddNode extends BinaryNode {

  private static final long serialVersionUID = 1918160088428325814L;

  /**
   * Instantiates a new adds the node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public AddNode(INode left, INode right) {
    super(left, right);
  }

  @Override
  public INode calc(ValueNode<?> l, ValueNode<?> r) {
    boolean d = l.isDouble() || r.isDouble();

    if (d) {
      try {
        return new ValueNode<>(((Number) l.getValue()).doubleValue()
            + ((Number) r.getValue()).doubleValue());
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }

    return new ValueNode<>((Integer) l.getValue() + (Integer) r.getValue());
  }

  // @Override
  // public Object calc(List<INode> nodes) {
  // ValueNode<Double> l = (ValueNode<Double>) nodes.get(0);
  // ValueNode<Double> r = (ValueNode<Double>) nodes.get(1);
  //
  // if (l.isDouble() && r.isDouble())
  // return (Double) l + (Double)r;
  //
  // return (Integer) l + (Integer) r;
  // }

  @Override
  public String getName() {
    return "+";
  }
}
