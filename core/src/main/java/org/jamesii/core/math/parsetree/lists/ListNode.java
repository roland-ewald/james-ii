/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that represents a list.
 * 
 * @author Jan Himmelspach
 */
public class ListNode extends ValueNode<List<INode>> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -1020362449859902489L;

  /**
   * Instantiates a new list node.
   * 
   * @param val
   *          a list of nodes
   */
  public ListNode(List<INode> val) {
    super(val);
  }

  /**
   * Instantiates a new list node.
   * 
   * @param val
   *          a list of parameters (nodes)
   */
  public ListNode(INode... val) {
    super(Arrays.asList(val));
  }

  /**
   * Instantiates a new list node.
   */
  public ListNode() {
    super(new ArrayList<INode>());
  }

  /**
   * Adds all elements from given list.
   * 
   * @param list
   *          the list
   */
  public void addAll(List<INode> list) {
    getValue().addAll(list);
  }

  /**
   * Adds all elements from given ListNode.
   * 
   * @param list
   *          the ListNode
   */
  public void addAll(ListNode list) {
    getValue().addAll(list.getValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {

    List<INode> result = new ArrayList<>();

    for (INode node : getValue()) {
      result.add(node.calc(cEnv));
    }
    return (N) new ListNode(result);
  }

  @Override
  public String toString() {
    return getValue().toString();
  }

  @Override
  public List<INode> getChildren() {
    return getValue();
  }

}
