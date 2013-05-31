/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.lists;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Returns the head node of the list of children nodes.
 * 
 * @author Jan Himmelspach
 */
public class HeadNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 7559822259074518539L;

  /**
   * Instantiates a new head node.
   * 
   * @param val
   *          the val
   */
  public HeadNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ListNode result = getValue().calc(cEnv);
    return (N) result.getValue().get(0);
  }

  @Override
  public String getName() {
    return "head";
  }
}
