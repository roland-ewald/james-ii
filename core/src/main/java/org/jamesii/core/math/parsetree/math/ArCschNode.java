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
 * Node that represents the area cosecans hyperbolicus operation.
 * 
 * @author Oliver Röwer
 */
public class ArCschNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 2131396301081080652L;

  /**
   * Instantiates a new arcsch node.
   * 
   * @param val
   *          the val
   */
  public ArCschNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double returnValue;
    if (Double.compare(x, 0.0) == 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = Math.log(1.0 / x + Math.sqrt(1.0 + 1.0 / Math.pow(x, 2)));
    }

    return (N) new ValueNode<>(returnValue);
  }

}
