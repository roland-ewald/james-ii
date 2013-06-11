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
 * Returns the number of elements in the list which are equal to the node. All
 * nodes have to be of type comparable!
 * 
 * @author Jan Himmelspach
 * @author Oliver Röwer
 */
public class HasElementsNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -2029736480689380504L;

  /** The list. */
  private INode list;

  /** The node. */
  private INode node;

  /**
   * Instantiates a new checks for elements node.
   * 
   * @param list
   *          the list
   * @param node
   *          the node
   */
  public HasElementsNode(INode list, INode node) {
    super(list);
    this.list = list;
    this.node = node;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ListNode result = getValue().calc(cEnv);
    ValueNode<? extends Comparable<Object>> n = node.calc(cEnv);

    int res = 0;

    for (INode cn : result.getValue()) {
      ValueNode<? extends Comparable<Object>> c =
          (ValueNode<? extends Comparable<Object>>) cn;
      if (c.getValue().compareTo(n.getValue()) == 0) {
        res++;
      }
    }

    return (N) new ValueNode<>(res);
  }

  @Override
  public String getName() {
    return "haselements";
  }

  @Override
  public List<INode> getChildren() {
    List<INode> result = new ArrayList<>();
    result.add(list);
    result.add(node);
    return result;
  }
}
