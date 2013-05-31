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
 * Node that represents the sinus operation.
 * 
 * @author Jan Himmelspach
 */
public class SinNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 7991149086437303048L;

  /**
   * Instantiates a new sin node.
   * 
   * @param val
   *          the val
   */
  public SinNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    return (N) new ValueNode<>(
        Math.sin(Double.valueOf(((Number) (v).getValue()).doubleValue())));
  }

}
