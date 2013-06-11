/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.core.math.parsetree.control;

import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.TestNode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.bool.OrNode;
import org.jamesii.core.math.parsetree.control.IfThenElseNode;

/**
 * The Class TestIfThenElseNode.
 */
public class TestIfThenElseNode extends TestNode {

  @Override
  public void testCalc() {
    IfThenElseNode ifN =
        new IfThenElseNode(new ValueNode<>(true), new ValueNode<>(5),
            new ValueNode<>(7));
    ValueNode<Integer> res = ifN.calc(null);
    assertTrue(res.getValue().compareTo(5) == 0);

    ifN =
        new IfThenElseNode(new ValueNode<>(false), new ValueNode<>(5),
            new ValueNode<>(7));
    res = ifN.calc(null);
    assertTrue(res.getValue().compareTo(7) == 0);

    ifN =
        new IfThenElseNode(new OrNode(new ValueNode<>(false), new ValueNode<>(
            false)), new ValueNode<>(5), new ValueNode<>(7));
    res = ifN.calc(null);
    assertTrue(res.getValue().compareTo(7) == 0);

    ifN =
        new IfThenElseNode(new OrNode(new ValueNode<>(true), new ValueNode<>(
            false)), new ValueNode<>(5), new ValueNode<>(7));
    res = ifN.calc(null);
    assertTrue(res.getValue().compareTo(5) == 0);

    ifN =
        new IfThenElseNode(new ValueNode<>(false), new ValueNode<>(5),
            new OrNode(new ValueNode<>(true), new ValueNode<>(true)));
    ValueNode<Boolean> res2 = ifN.calc(null);
    assertTrue(res2.getValue().compareTo(true) == 0);

    ifN =
        new IfThenElseNode(new ValueNode<>(true), new OrNode(new ValueNode<>(
            true), new ValueNode<>(true)), new ValueNode<>(5));
    res2 = ifN.calc(null);
    assertTrue(res2.getValue().compareTo(true) == 0);
  }

  @Override
  public void testClone() {
    Node cond = new ValueNode<>(true);
    Node then = new ValueNode<>(5);
    Node els = new ValueNode<>(7);
    IfThenElseNode ifN = new IfThenElseNode(cond, then, els);

    Node n2 = null;
    try {
      n2 = (Node) ifN.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(ifN != n2);

    // TODO check clone (deep cloning???)!!!
    // List<Node> res = ifN.getChildren();
    // List<Node> resClone = n2.getChildren();
    //
    // assertTrue (res.get(0) != resClone.get(0));
    // assertTrue (res.get(1) != resClone.get(1));
    // assertTrue (res.get(2) != resClone.get(2));

  }

  public void testGetters() {
    Node cond = new ValueNode<>(true);
    Node then = new ValueNode<>(5);
    Node els = new ValueNode<>(7);
    IfThenElseNode ifN = new IfThenElseNode(cond, then, els);
    assertEquals(ifN.getElseStmt(), els);
    assertEquals(ifN.getThenStmt(), then);
    assertEquals(ifN.getCondition(), cond);
  }

  @Override
  public void testGetChildren() {
    Node cond = new ValueNode<>(true);
    Node then = new ValueNode<>(5);
    Node els = new ValueNode<>(7);
    IfThenElseNode ifN = new IfThenElseNode(cond, then, els);
    List<INode> res = ifN.getChildren();
    assertTrue(res.size() == 3);
    assertTrue(res.get(0) == cond);
    assertTrue(res.get(1) == then);
    assertTrue(res.get(2) == els);
  }

}
