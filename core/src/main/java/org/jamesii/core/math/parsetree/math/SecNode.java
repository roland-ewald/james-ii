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
 * Node that represents the secans operation.
 * 
 * @author Oliver Röwer
 */
public class SecNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -5064514697141956003L;

  /**
   * Instantiates a new sec node.
   * 
   * @param val
   *          the val
   */
  public SecNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double cosX = Math.cos(x);

    Double returnValue;
    if (cosX.compareTo(0.0) == 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = 1.0 / cosX;
    }

    return (N) new ValueNode<>(returnValue);
  }

}
