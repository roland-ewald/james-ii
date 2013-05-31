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
 * Node that represents the cotangens hyperbolicus operation.
 * 
 * @author Oliver Röwer
 */
public class CothNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -7483078214295820257L;

  /**
   * Instantiates a new coth node.
   * 
   * @param val
   *          the val
   */
  public CothNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double sinhX = Math.sinh(x);

    Double returnValue;
    if (sinhX.compareTo(0.0) == 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = Math.cosh(x) / sinhX;
    }

    return (N) new ValueNode<>(returnValue);
  }

}
