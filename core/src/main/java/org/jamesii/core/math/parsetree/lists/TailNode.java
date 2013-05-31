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
 * Node that represents the tail (returning the list without the first element)
 * operation on a list.
 * 
 * @author Jan Himmelspach
 */
public class TailNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -5906253514662974917L;

  /**
   * Instantiates a new tail node.
   * 
   * @param val
   *          the val
   */
  public TailNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ListNode result = getValue().calc(cEnv);
    result.getValue().remove(0);
    return (N) result;
  }

  @Override
  public String getName() {
    return "tail";
  }

}
