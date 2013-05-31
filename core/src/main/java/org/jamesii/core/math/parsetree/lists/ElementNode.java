/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.lists;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that returns the element of a list given by the list and node nodes.
 * 
 * @author Jan Himmelspach
 * @author Oliver Röwer
 */
public class ElementNode extends ValueNode<INode> {

  private static final long serialVersionUID = -1585608876237378748L;

  /** The list. */
  private INode list;

  /** The node. */
  private INode node;

  /**
   * Instantiates a new element node.
   * 
   * @param list
   *          the list
   * @param node
   *          the node
   */
  public ElementNode(INode list, INode node) {
    super(list);
    this.list = list;
    this.node = node;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ListNode result = getValue().calc(cEnv);
    ValueNode<Integer> n = node.calc(cEnv);

    return (N) new ValueNode<>(result.getValue().get(n.getValue()));
  }

  @Override
  public String getName() {
    return "elem";
  }

  @Override
  public List<INode> getChildren() {
    List<INode> result = new ArrayList<>();
    result.add(list);
    result.add(node);
    return result;
  }

  /**
   * @return the node
   */
  protected final INode getNode() {
    return node;
  }

}
