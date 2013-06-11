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
import org.jamesii.core.math.parsetree.lists.TailNode;
import org.jamesii.core.math.parsetree.math.MultNode;

/**
 * The Class TestTailNode.
 */
public class TestTailNode extends TestValueNodeAbstract<TailNode, INode> {

  @SuppressWarnings("unchecked")
  @Override
  public void testCalc() {
    INode node = getInstance(getA(0));
    ListNode res = node.calc(null);
    List<INode> resList = res.getValue();
    assertTrue(resList.size() == 0);

    node = getInstance(getA(3));
    res = node.calc(null);
    resList = res.getValue();
    assertTrue(resList.size() == 1);
    assertTrue(((ValueNode<Double>) resList.get(0)).getValue().compareTo(2.0) == 0);

    node =
        getInstance(new ListNode(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)))));
    res = node.calc(null);
    resList = res.getValue();
    assertTrue(resList.size() == 0);

    node =
        getInstance(new ListNode(toList(new MultNode(new ValueNode<>(2),
            new ValueNode<>(-2.)), new MultNode(new ValueNode<>(2),
            new ValueNode<>(-3.)))));
    res = node.calc(null);
    resList = res.getValue();
    assertTrue(resList.size() == 1);
    assertTrue(((ValueNode<Double>) resList.get(0)).getValue().compareTo(-6.0) == 0);
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
  protected TailNode getInstance(INode content) {
    return new TailNode(content);
  }

  @Override
  public void testGetChildren() {
    TailNode bN = getInstance(getA(0));
    assertTrue(bN.getChildren() != null);
    assertTrue(bN.getChildren().size() == 1);
  }

  @Override
  public void testClone() {
    TailNode n1 = getInstance(getA(0));
    TailNode n2 = null;
    try {
      n2 = (TailNode) n1.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(n1 != n2);
  }

}
