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
 * Node that represents the absolute operation.
 * 
 * @author Jan Himmelspach
 */
public class AbsNode extends ValueNode<INode> {

  private static final long serialVersionUID = -355281302449281967L;

  /**
   * Instantiates a new abs node.
   * 
   * @param val
   *          the val
   */
  public AbsNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    return (N) new ValueNode<>(
        Math.abs(Double.valueOf(((Number) (v).getValue()).doubleValue())));
  }

  @Override
  public String getName() {
    return "ABS";
  }

}
