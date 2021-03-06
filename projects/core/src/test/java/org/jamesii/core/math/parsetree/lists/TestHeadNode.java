/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.TestValueNodeAbstract;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.lists.HeadNode;
import org.jamesii.core.math.parsetree.lists.ListNode;
import org.jamesii.core.math.parsetree.math.MultNode;

/**
 * The Class TestHeadNode.
 */
public class TestHeadNode extends TestValueNodeAbstract<HeadNode, INode> {

  @Override
  public void testCalc() {
    Node node = getInstance(getA(0));
    ValueNode<Integer> res = node.calc(null);
    assertTrue(res.getValue().compareTo(1) == 0);

    node = getInstance(getA(1));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(-1) == 0);

    node = getInstance(getA(2));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(0) == 0);

    node = getInstance(getA(3));
    ValueNode<Double> res2 = node.calc(null);
    assertTrue(res2.getValue().compareTo(1.) == 0);

    node =
        getInstance(new ListNode(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)))));
    res2 = node.calc(null);
    assertTrue(res2.getValue().compareTo(-4.) == 0);

    node =
        getInstance(new ListNode(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)), new MultNode(new ValueNode<>(2),
            new ValueNode<>(-3.)))));
    res2 = node.calc(null);
    assertTrue(res2.getValue().compareTo(-4.) == 0);
  }

  private List<INode> toList(Node... nodes) {
    ArrayList<INode> result = new ArrayList<>();
    Collections.addAll(result, nodes);
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
  protected HeadNode getInstance(INode content) {
    return new HeadNode(content);
  }

  @Override
  public void testGetChildren() {
    HeadNode bN = getInstance(getA(0));
    assertTrue(bN.getChildren() != null);
    assertTrue(bN.getChildren().size() == 1);
  }

  @Override
  public void testClone() {
    HeadNode n1 = getInstance(getA(0));
    HeadNode n2 = null;
    try {
      n2 = (HeadNode) n1.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(n1 != n2);
  }

}
