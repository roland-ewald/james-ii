/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.bool;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;

/**
 * Node that represents a boolean "implies" operation.
 * 
 * @author Stefan Leye
 */
public class ImpliesNode extends BinaryNode {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 5562582034034556044L;

  /**
   * Instantiates a new implies node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public ImpliesNode(INode left, INode right) {
    super(left, right);
  }

  @Override
  public INode calc(ValueNode<?> l, ValueNode<?> r) {

    Boolean result;
    Boolean lv = null;
    Boolean rv = null;

    if (l.getValue() instanceof Boolean) {
      lv = (Boolean) l.getValue();
    }

    if (l.getValue() instanceof Number) {
      lv =
          Double.valueOf(((Number) l.getValue()).doubleValue()).compareTo(0.) > 0;
    }

    if (r.getValue() instanceof Boolean) {
      rv = (Boolean) r.getValue();
    }

    if (r.getValue() instanceof Number) {
      rv =
          Double.valueOf(((Number) r.getValue()).doubleValue()).compareTo(0.) > 0;
    }

    if ((lv == null) || (rv == null)) {
      return null;
    }

    result = !lv || rv;

    return new ValueNode<>(result);

  }

  @Override
  public String getName() {
    return " IMPLIES ";
  }

}
