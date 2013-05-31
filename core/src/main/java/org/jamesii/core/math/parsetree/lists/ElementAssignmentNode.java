/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.lists;

import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node to represent the assignment of a value to an element of a list.
 * 
 * @author Stefan Leye
 */
public class ElementAssignmentNode extends ElementNode {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -413931000398717791L;

  /** The value to be assigned to the element. */
  private INode value;

  /**
   * Instantiates a new element assignment node.
   * 
   * @param list
   *          the list
   * @param node
   *          the node
   * @param value
   *          the value
   */
  public ElementAssignmentNode(INode list, INode node, INode value) {
    super(list, node);
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ListNode listNode = getValue().calc(cEnv);
    ValueNode<Integer> elem = (ValueNode<Integer>) getNode().calc(cEnv);
    List<INode> nodeList = listNode.getValue();
    nodeList.remove(elem.getValue());
    nodeList.add(elem.getValue(), value);
    return (N) value;
  }

  @Override
  public List<INode> getChildren() {
    List<INode> result = super.getChildren();
    result.add(value);
    return result;
  }

  @Override
  public String getName() {
    return "eleAss";
  }
}
