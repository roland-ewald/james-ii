/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.bool;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that represents a boolean "not" operation.
 * 
 * @author Jan Himmelspach
 */
public class NotNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -6021279553838609249L;

  /**
   * Instantiates a new not node.
   * 
   * @param val
   *          the val
   */
  public NotNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<Boolean> v = getValue().calc(cEnv);
    return (N) new ValueNode<>(!v.getValue().booleanValue());
  }

  @Override
  public String getName() {
    return "not";
  }

}
