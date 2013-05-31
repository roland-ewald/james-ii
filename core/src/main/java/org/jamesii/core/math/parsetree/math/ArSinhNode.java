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
 * Node that represents the area sinus hyperbolicus operation.
 * 
 * @author Oliver Röwer
 */
public class ArSinhNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 5994259127557569863L;

  /**
   * Instantiates a new arsinh node.
   * 
   * @param val
   *          the val
   */
  public ArSinhNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double returnValue = Math.log(x + Math.sqrt(Math.pow(x, 2) + 1.0));
    return (N) new ValueNode<>(returnValue);
  }

}
