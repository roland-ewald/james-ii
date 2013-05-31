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
import org.jamesii.core.math.parsetree.lists.ListNode;
import org.jamesii.core.math.parsetree.math.MultNode;

/**
 * The Class TestListNode.
 */
public class TestListNode extends TestValueNodeAbstract<ListNode, List<INode>> {

  @SuppressWarnings("unchecked")
  @Override
  public void testCalc() {
    Node node = getInstance(getA(0));
    ListNode res = node.calc(null);
    List<INode> resList = res.getValue();
    assertTrue(((ValueNode<Integer>) resList.get(0)).getValue().compareTo(1) == 0);

    node = getInstance(getA(1));
    res = node.calc(null);
    resList = res.getValue();
    assertTrue(((ValueNode<Integer>) resList.get(0)).getValue().compareTo(-1) == 0);

    node = getInstance(getA(2));
    res = node.calc(null);
    resList = res.getValue();
    assertTrue(((ValueNode<Integer>) resList.get(0)).getValue().compareTo(0) == 0);

    node = getInstance(getA(3));
    ListNode res2 = node.calc(null);
    resList = res2.getValue();
    assertTrue(((ValueNode<Double>) resList.get(0)).getValue().compareTo(1.) == 0);

    node = getInstance(getA(4));
    res2 = node.calc(null);
    resList = res2.getValue();
    assertTrue(((ValueNode<Double>) resList.get(0)).getValue().compareTo(-1.) == 0);

    node = getInstance(getA(5));
    res2 = node.calc(null);
    resList = res2.getValue();
    assertTrue(((ValueNode<Double>) resList.get(0)).getValue().compareTo(0.) == 0);

    node =
        getInstance(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.))));
    res2 = node.calc(null);
    resList = res2.getValue();
    assertTrue(((ValueNode<Double>) resList.get(0)).getValue().compareTo(-4.) == 0);

    node =
        getInstance(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)), new MultNode(new ValueNode<>(2),
            new ValueNode<>(-3.))));
    res2 = node.calc(null);
    resList = res2.getValue();
    assertTrue(((ValueNode<Double>) resList.get(0)).getValue().compareTo(-4.) == 0);
    assertTrue(((ValueNode<Double>) resList.get(1)).getValue().compareTo(-6.) == 0);
  }

  private List<INode> toList(Node... nodes) {
    ArrayList<INode> result = new ArrayList<>();
    for (Node n : nodes) {
      result.add(n);
    }
    return result;
  }

  @Override
  protected List<INode> getA(int index) {
    switch (index) {
    case 0: {
      return toList(new ValueNode<>(1));
    }
    case 1: {
      return toList(new ValueNode<>(-1));
    }
    case 2: {
      return toList(new ValueNode<>(0));
    }
    case 3: {
      return toList(new ValueNode<>(1.));
    }
    case 4: {
      return toList(new ValueNode<>(-1.));
    }
    case 5: {
      return toList(new ValueNode<>(0.));
    }
    }

    return null;
  }

  public void testAddAll() {
    ListNode node =
        getInstance(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)), new MultNode(new ValueNode<>(2),
            new ValueNode<>(-3.))));
    int size = node.getValue().size();
    List<INode> n;
    node.addAll(n = getA(0));
    assertEquals(size + 1, node.getValue().size());
    assertEquals(node.getValue().get(size), n.get(0));

    ListNode node2 =
        getInstance(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)), new MultNode(new ValueNode<>(2),
            new ValueNode<>(-3.))));
    node.addAll(node2);

    assertEquals(size + 1 + node2.getValue().size(), node.getValue().size());

    ListNode node3 =
        new ListNode(new ValueNode<>(1), new ValueNode<>(1), new ValueNode<>(1));
    assertEquals(node3.getValue().size(), 3);
  }

  @Override
  protected ListNode getInstance(List<INode> content) {
    return new ListNode(content);
  }

  @Override
  public void testGetChildren() {
    MultNode m1;
    MultNode m2;
    ListNode node =
        getInstance(toList(m1 =
            new MultNode(new ValueNode<>(2), new ValueNode<>(-2.)), m2 =
            new MultNode(new ValueNode<>(2), new ValueNode<>(-3.))));
    assertEquals(node.getChildren().get(0), m1);
    assertEquals(node.getChildren().get(1), m2);
  }

  @Override
  public void testClone() {
    ListNode node =
        getInstance(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)), new MultNode(new ValueNode<>(2),
            new ValueNode<>(-3.))));
    try {
      ListNode node2 = (ListNode) node.clone();
      assertFalse(node == node2);

      // assertFalse (node.getChildren() == node2.getChildren());

    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
  }

}
