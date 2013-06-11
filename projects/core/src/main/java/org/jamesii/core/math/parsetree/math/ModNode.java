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
 * Node that represents the modulo operation.
 * 
 * @author Jan Himmelspach
 */
public class ModNode extends BinaryNode {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -4063042564065141927L;

  /**
   * Instantiates a new mod node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public ModNode(INode left, INode right) {
    super(left, right);
  }

  @Override
  public INode calc(ValueNode<?> l, ValueNode<?> r) {

    // boolean d = l.isDouble() || r.isDouble();
    //
    // if (d) {
    // return new ValueNode<Double>((Double)l.getValue() %
    // (Double)r.getValue());
    // }

    return new ValueNode<>((Integer) l.getValue() % (Integer) r.getValue());
  }

  @Override
  public String getName() {
    return "MOD";
  }

}
