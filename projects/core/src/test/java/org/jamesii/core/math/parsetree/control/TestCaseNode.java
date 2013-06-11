/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.core.math.parsetree.control;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.TestNode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.control.CaseNode;
import org.jamesii.core.math.parsetree.math.AddNode;
import org.jamesii.core.util.misc.Pair;

/**
 * The Class TestCaseNode.
 */
public class TestCaseNode extends TestNode {

  @Override
  public void testCalc() {
    List<Pair<? extends Comparable<?>, INode>> caseTerms = new ArrayList<>();
    caseTerms.add(new Pair<Comparable<?>, INode>(2, new ValueNode<>(3)));
    caseTerms.add(new Pair<Comparable<?>, INode>(1, new AddNode(
        new ValueNode<>(3), new ValueNode<>(3))));

    CaseNode cn = new CaseNode(new ValueNode<>(2), caseTerms);
    ValueNode<Integer> res = cn.calc(null);
    assertTrue(res.getValue().compareTo(3) == 0);

    cn = new CaseNode(new ValueNode<>(1), caseTerms);
    res = cn.calc(null);
    assertTrue(res.getValue().compareTo(6) == 0);

    cn = new CaseNode(new ValueNode<>(4), caseTerms);
    res = cn.calc(null);
    assertTrue(res == null);

    caseTerms.add(new Pair<Comparable<?>, INode>(null, new ValueNode<>(31)));
    cn = new CaseNode(new ValueNode<>(4), caseTerms);
    res = cn.calc(null);
    assertTrue(res.getValue().compareTo(31) == 0);
  }

  @Override
  public void testClone() {
    ValueNode<Integer> caseStmt = new ValueNode<>(2);
    List<Pair<? extends Comparable<?>, INode>> caseTerms = new ArrayList<>();
    caseTerms.add(new Pair<Comparable<?>, INode>(2, new ValueNode<>(3)));
    caseTerms.add(new Pair<Comparable<?>, INode>(1, new AddNode(
        new ValueNode<>(3), new ValueNode<>(3))));
    CaseNode cn = new CaseNode(caseStmt, caseTerms);

    Node n2 = null;
    try {
      n2 = (Node) cn.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(cn != n2);

  }

  public void testGetCaseStmtTerms() {
    ValueNode<Integer> caseStmt = new ValueNode<>(2);
    List<Pair<? extends Comparable<?>, INode>> caseTerms = new ArrayList<>();
    caseTerms.add(new Pair<Comparable<?>, INode>(2, new ValueNode<>(3)));
    caseTerms.add(new Pair<Comparable<?>, INode>(1, new AddNode(
        new ValueNode<>(3), new ValueNode<>(3))));
    CaseNode cn = new CaseNode(caseStmt, caseTerms);
    assertEquals(caseStmt, cn.getCaseStmt());
    assertEquals(caseTerms, cn.getCaseTerms());
  }

  @Override
  public void testGetChildren() {
    ValueNode<Integer> caseStmt = new ValueNode<>(2);
    List<Pair<? extends Comparable<?>, INode>> caseTerms = new ArrayList<>();
    caseTerms.add(new Pair<Comparable<?>, INode>(2, new ValueNode<>(3)));
    caseTerms.add(new Pair<Comparable<?>, INode>(1, new AddNode(
        new ValueNode<>(3), new ValueNode<>(3))));
    CaseNode cn = new CaseNode(caseStmt, caseTerms);
    List<INode> children = cn.getChildren();
    assertTrue(children.size() == 3);
    assertTrue(children.get(0) == caseStmt);
    assertTrue(children.get(1) == caseTerms.get(0).getSecondValue());
    assertTrue(children.get(2) == caseTerms.get(1).getSecondValue());
  }

}
