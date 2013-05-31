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
 * Node that represents the tangens hyperbolicus operation.
 * 
 * @author Jan Himmelspach
 */
public class TanhNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 583117572493399612L;

  /**
   * Instantiates a new tanh node.
   * 
   * @param val
   *          the val
   */
  public TanhNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    return (N) new ValueNode<>(Math.tanh(Double.valueOf(((Number) (v)
        .getValue()).doubleValue())));
  }

}
