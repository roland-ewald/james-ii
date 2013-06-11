/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree;

import org.jamesii.core.math.parsetree.ValueNode;

/**
 * The Class TestBinaryNode.
 */
public class TestValueNode extends TestNode {

  /**
   * Gets the single instance of TestValueNode.
   * 
   * @param <A>
   * 
   * @param content
   *          the content
   * 
   * @return single instance of TestValueNode
   */
  protected <A> ValueNode<A> getInstance(A content) {
    return new ValueNode<>(content);
  }

  /**
   * Test getValue().
   */
  public void testGetValue() {
    ValueNode<Boolean> bN = getInstance(true);
    assertTrue(bN.getValue() == true);

    ValueNode<Integer> bI = getInstance(42);
    assertTrue(bI.getValue() == 42);
  }

  /**
   * Test setValue.
   */
  public void testSetValue() {
    ValueNode<Boolean> bN = getInstance(true);
    bN.setValue(false);
    assertTrue(bN.getValue() == false);

    ValueNode<Integer> bI = getInstance(42);
    bI.setValue(24);
    assertTrue(bI.getValue() == 24);
  }

  @Override
  public void testGetChildren() {
    ValueNode<Boolean> bN = getInstance(true);
    assertTrue(bN.getChildren() != null);
    assertTrue(bN.getChildren().size() == 0);

    ValueNode<ValueNode<Boolean>> bNode = getInstance(bN);
    assertTrue(bNode.getChildren() != null);
    assertTrue(bNode.getChildren().size() == 1);
    assertTrue(bNode.getChildren().contains(bN));
  }

  /**
   * Test isDouble.
   */
  public void testIsDouble() {
    ValueNode<Boolean> bN = getInstance(true);
    assertFalse(bN.isDouble());

    ValueNode<Double> bD = getInstance(2.3);
    assertTrue(bD.isDouble());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void testClone() {
    ValueNode<Double> n1 = getInstance(2.3);
    ValueNode<Double> n2 = null;
    try {
      n2 = (ValueNode<Double>) n1.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(n1 != n2);
    assertTrue(n1.getValue().compareTo(n2.getValue()) == 0);

    ValueNode<ValueNode<Double>> n3 = getInstance(n1);
    ValueNode<ValueNode<Double>> n4 = null;

    try {
      n4 = (ValueNode<ValueNode<Double>>) n3.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(n3 != n4);
    // assertTrue (n3.getValue() != n4.getValue());
  }

  @Override
  public void testCalc() {
    ValueNode<Integer> bI = getInstance(42);
    ValueNode<ValueNode<Integer>> bI2 = getInstance(bI);
    ValueNode<Integer> res = bI2.calc(null);
    assertTrue(res.getValue() == 42);
  }

}
