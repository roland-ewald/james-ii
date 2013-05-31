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
 * Node that represents the arcus secans operation.
 * 
 * @author Oliver Röwer
 */
public class ArcSecNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 5414019760743497873L;

  /**
   * Instantiates a new arcsec node.
   * 
   * @param val
   *          the val
   */
  public ArcSecNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double returnValue;
    if (Double.compare(Math.abs(x), 1.0) < 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = Math.acos(1.0 / x);
    }

    return (N) new ValueNode<>(returnValue);
  }

}
