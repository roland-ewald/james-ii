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
 * Node that represents the area cosinus hyperbolicus operation.
 * 
 * @author Oliver Röwer
 */
public class ArCoshNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 8945735785631613866L;

  /**
   * Instantiates a new acosh node.
   * 
   * @param val
   *          the val
   */
  public ArCoshNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();
    return (N) new ValueNode<>(Math.log(x + Math.sqrt(Math.pow(x, 2) - 1)));
  }

}
