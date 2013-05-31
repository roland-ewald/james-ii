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
 * Append node to the list of nodes. Thereby list and node to be added are
 * "calculated" before the node is appended.
 * 
 * @author Jan Himmelspach
 */
public class AppendNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -5728663432614990916L;

  /** The list. */
  private INode list;

  /** The node. */
  private INode node;

  /**
   * Instantiates a new append node.
   * 
   * @param list
   *          the list
   * @param node
   *          the node
   */
  public AppendNode(INode list, INode node) {
    super(list);
    this.list = list;
    this.node = node;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<Object> n = list.calc(cEnv);

    ValueNode<Object> n2 = node.calc(cEnv);

    List<Object> l = (List<Object>) n.getValue();
    l.add(n2.getValue());

    return (N) new ValueNode<>(l);
  }

  @Override
  public String getName() {
    return "append";
  }

  @Override
  public List<INode> getChildren() {
    List<INode> result = new ArrayList<>();
    result.add(list);
    result.add(node);
    return result;
  }
}
