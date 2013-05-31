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
 * Node that represents the arcus cosecans operation.
 * 
 * @author Oliver Röwer
 */
public class ArcCscNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 3579585609701072059L;

  /**
   * Instantiates a new arccsc node.
   * 
   * @param val
   *          the val
   */
  public ArcCscNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double returnValue;
    if (Math.abs(x) < 1.0) {
      returnValue = Double.NaN;
    } else {
      returnValue = Math.asin(1.0 / x);
    }

    return (N) new ValueNode<>(returnValue);
  }

}
