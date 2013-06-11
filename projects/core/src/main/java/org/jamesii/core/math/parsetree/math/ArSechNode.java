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
 * Node that represents the area secans hyperbolicus operation.
 * 
 * @author Oliver Röwer
 */
public class ArSechNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -2629199615240740820L;

  /**
   * Instantiates a new arsech node.
   * 
   * @param val
   *          the val
   */
  public ArSechNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double returnValue;
    if ((Double.compare(x, 0.0) <= 0) || (Double.compare(1.0, x) < 0)) {
      returnValue = Double.NaN;
    } else {
      returnValue = Math.log((1.0 + Math.sqrt(1.0 - Math.pow(x, 2))) / x);
    }

    return (N) new ValueNode<>(returnValue);
  }

}
