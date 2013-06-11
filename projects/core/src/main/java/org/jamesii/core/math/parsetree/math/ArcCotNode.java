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
 * INode that represents the arcus cotangens operation.
 * 
 * @author Oliver Röwer
 */
public class ArcCotNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -1945635857371716045L;

  /**
   * Instantiates a new arccot node.
   * 
   * @param val
   *          the val
   */
  public ArcCotNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double returnValue = Math.PI / 2.0 - Math.atan(x);

    return (N) new ValueNode<>(returnValue);
  }
}
