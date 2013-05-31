/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that represents the secans hyperbolicus operation.
 * 
 * @author Oliver Röwer
 */
public class SechNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 7567086848173651665L;

  /**
   * Instantiates a new sech node.
   * 
   * @param val
   *          the val
   */
  public SechNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double cosH = Math.cosh(x);

    Double returnValue;
    if (cosH.compareTo(0.0) == 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = 1.0 / cosH;
    }

    return (N) new ValueNode<>(returnValue);
  }

}
