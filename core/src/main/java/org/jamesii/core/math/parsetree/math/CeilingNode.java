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
 * Node that represents the flceilingoor operation.
 * 
 * @author Jan Himmelspach
 */
public class CeilingNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 6349606207036204830L;

  /**
   * Instantiates a new ceiling node.
   * 
   * @param val
   *          the val
   */
  public CeilingNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    return (N) new ValueNode<>(Math.ceil(Double.valueOf(((Number) (v)
        .getValue()).doubleValue())));
  }

}
