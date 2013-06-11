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
 * Node that represents the length of a node.
 * 
 * @author Jan Himmelspach
 */
public class LengthNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 3904184345551184645L;

  /**
   * Instantiates a new length node.
   * 
   * @param list
   *          the list
   */
  public LengthNode(INode list) {
    super(list);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ListNode result = getValue().calc(cEnv);
    return (N) new ValueNode<>(result.getValue().size());
  }

  @Override
  public String getName() {
    return "length";
  }
}
