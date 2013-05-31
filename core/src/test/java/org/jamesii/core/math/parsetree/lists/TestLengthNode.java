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
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.TestValueNodeAbstract;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.lists.LengthNode;
import org.jamesii.core.math.parsetree.lists.ListNode;
import org.jamesii.core.math.parsetree.math.MultNode;

/**
 * The Class TestLengthNode.
 */
public class TestLengthNode extends TestValueNodeAbstract<LengthNode, INode> {

  @Override
  public void testCalc() {
    Node node = getInstance(getA(0));
    ValueNode<Integer> res = node.calc(null);
    assertTrue(res.getValue().compareTo(1) == 0);

    node = getInstance(getA(1));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(1) == 0);

    node = getInstance(getA(2));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(1) == 0);

    node = getInstance(getA(3));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(2) == 0);

    node =
        getInstance(new ListNode(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)))));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(1) == 0);

    node =
        getInstance(new ListNode(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)), new MultNode(new ValueNode<>(2),
            new ValueNode<>(-3.)))));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(2) == 0);
  }

  private List<INode> toList(Node... nodes) {
    ArrayList<INode> result = new ArrayList<>();
    for (Node n : nodes) {
      result.add(n);
    }
    return result;
  }

  @Override
  protected ListNode getA(int index) {
    switch (index) {
    case 0: {
      return new ListNode(toList(new ValueNode<>(1)));
    }
    case 1: {
      return new ListNode(toList(new ValueNode<>(-1)));
    }
    case 2: {
      return new ListNode(toList(new ValueNode<>(0)));
    }
    case 3: {
      return new ListNode(toList(new ValueNode<>(1.), new ValueNode<>(2.)));
    }
    case 4: {
      return new ListNode(toList(new ValueNode<>(-1.)));
    }
    case 5: {
      return new ListNode(toList(new ValueNode<>(0.)));
    }
    }

    return null;
  }

  @Override
  protected LengthNode getInstance(INode content) {
    return new LengthNode(content);
  }

  @Override
  public void testGetChildren() {
    LengthNode bN = getInstance(getA(0));
    assertTrue(bN.getChildren() != null);
    assertTrue(bN.getChildren().size() == 1);
  }

  @Override
  public void testClone() {
    LengthNode n1 = getInstance(getA(0));
    LengthNode n2 = null;
    try {
      n2 = (LengthNode) n1.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(n1 != n2);
  }

}
