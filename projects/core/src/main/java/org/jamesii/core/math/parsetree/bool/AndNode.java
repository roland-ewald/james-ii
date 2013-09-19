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
 * Node that represents a boolean "and" operation. Thus
 * {@link #calc(org.jamesii.core.math.parsetree.variables.IEnvironment)} returns
 * a ValueNode<Boolean> with a value of <code>true</code> if the "and" operation
 * on the two nodes returns <code>true</code>, otherwise a ValueNode<Boolean>
 * with a <code>false</code> value is returned. <br>
 * Child nodes can be of type <code>Boolean</code> or they can be of any
 * <code>Number</code> type. In this case a value > 0 is considered to be
 * equivalent to <code>true</code>, a value <= 0 is considered to be
 * <code>false</code>.
 * 
 * @author Jan Himmelspach
 */
public class AndNode extends BinaryNode {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4283906907153652618L;

  /**
   * Instantiates a new and node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public AndNode(INode left, INode right) {
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

    result = lv && rv;

    return new ValueNode<>(result);

  }

  @Override
  public String getName() {
    return " AND ";
  }

}
