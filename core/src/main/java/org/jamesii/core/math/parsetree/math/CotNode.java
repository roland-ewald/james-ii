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
 * Node that represents the cotangens operation.
 * 
 * @author Oliver Röwer
 */
public class CotNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -2582680318117212299L;

  /**
   * Instantiates a new cot node.
   * 
   * @param val
   *          the val
   */
  public CotNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double sinX = Math.sin(x);

    Double returnValue;
    if (sinX.compareTo(0.0) == 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = Math.cos(x) / sinX;
    }

    return (N) new ValueNode<>(returnValue);
  }

}
