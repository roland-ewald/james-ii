/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree;

import java.util.List;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.ValueNode;

/**
 * The Class TestBinaryNode.
 */
public abstract class TestBinaryNode extends TestNode {

  /**
   * Gets the single instance of TestBinaryNode.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   * 
   * @return single instance of TestBinaryNode
   */
  protected abstract BinaryNode getInstance(INode left, INode right);

  @Override
  public abstract void testCalc();

  /**
   * Test get right.
   */
  public void testGetRight() {
    ValueNode<Integer> a = new ValueNode<>(2);
    ValueNode<Integer> b = new ValueNode<>(4);
    BinaryNode testNode = getInstance(a, b);
    assertTrue(testNode.getRight() == b);
  }

  /**
   * Test get left.
   */
  public void testGetLeft() {
    ValueNode<Integer> a = new ValueNode<>(2);
    ValueNode<Integer> b = new ValueNode<>(4);
    BinaryNode testNode = getInstance(a, b);
    assertTrue(testNode.getLeft() == a);
  }

  /**
   * Test set right.
   */
  public void testSetRight() {
    ValueNode<Integer> a = new ValueNode<>(2);
    ValueNode<Integer> b = new ValueNode<>(4);
    ValueNode<Integer> c = new ValueNode<>(6);
    BinaryNode testNode = getInstance(a, b);
    testNode.setRight(c);
    assertTrue(testNode.getRight() == c);
  }

  /**
   * Test set left.
   */
  public void testSetLeft() {
    ValueNode<Integer> a = new ValueNode<>(2);
    ValueNode<Integer> b = new ValueNode<>(4);
    ValueNode<Integer> c = new ValueNode<>(6);
    BinaryNode testNode = getInstance(a, b);
    testNode.setLeft(c);
    assertTrue(testNode.getLeft() == c);
  }

  /**
   * Test get op.
   */
  public void testGetOp() {
    ValueNode<Integer> a = new ValueNode<>(2);
    ValueNode<Integer> b = new ValueNode<>(4);
    BinaryNode testNode = getInstance(a, b);
    assertTrue(testNode.getName() != null);
  }

  /**
   * Test to string.
   */
  public void testToString() {
    ValueNode<Integer> a = new ValueNode<>(2);
    ValueNode<Integer> b = new ValueNode<>(4);
    BinaryNode testNode = getInstance(a, b);
    assertTrue(testNode.toString() != null);
  }

  @Override
  public void testGetChildren() {
    ValueNode<Integer> a = new ValueNode<>(2);
    ValueNode<Integer> b = new ValueNode<>(4);
    BinaryNode testNode = getInstance(a, b);
    List<INode> res = testNode.getChildren();
    assertTrue(res.size() == 2);
    assertTrue(res.contains(a));
    assertTrue(res.contains(b));
  }

  @Override
  public void testClone() {
    ValueNode<Integer> a = new ValueNode<>(2);
    ValueNode<Integer> b = new ValueNode<>(4);
    Node n1 = getInstance(a, b);
    Node n2 = null;
    try {
      n2 = (Node) n1.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(n1 != n2);

    // check equivalence
    // TODO
  }

}
