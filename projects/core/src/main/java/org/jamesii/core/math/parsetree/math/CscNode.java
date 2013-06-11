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
 * Node that represents the cosecans operation.
 * 
 * @author Oliver Röwer
 */
public class CscNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -6624627284977732758L;

  /**
   * Instantiates a new csc node.
   * 
   * @param val
   *          the val
   */
  public CscNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double sin_x = Math.sin(x);

    Double returnValue;
    if (sin_x.compareTo(0.0) == 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = 1.0 / sin_x;
    }

    return (N) new ValueNode<>(returnValue);
  }

}
