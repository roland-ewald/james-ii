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
 * 
 * @param <B>
 * @param <A>
 */
public abstract class TestValueNodeAbstract<B extends ValueNode<A>, A> extends
    TestNode {

  /**
   * Gets a single instance of ValueNode.
   * 
   * @param content
   *          the content
   * 
   * @return single instance of TestValueNode
   */
  protected abstract B getInstance(A content);

  /**
   * Gets the object to be used as value. The object returned has to be the same
   * object for all calls!! At index 0 it is not allowed to return sub nodes!
   * null is not allowed for index 0 as well!
   * 
   * @param index
   *          the index of the obj to be returned
   * 
   * @return the a
   */
  protected abstract A getA(int index);

  @Override
  public abstract void testCalc();

  /**
   * Test getValue().
   */
  public void testGetValue() {
    A a = getA(0);
    B bN = getInstance(a);
    assertEquals(bN.getValue(), a);
  }

  /**
   * Test setValue.
   */
  public void testSetValue() {
    B bN = getInstance(getA(0));
    A a = getA(1);
    bN.setValue(a);
    assertEquals(bN.getValue(), a);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void testGetChildren() {
    B bN = getInstance(getA(0));
    assertTrue(bN.getChildren() != null);
    assertTrue(bN.getChildren().size() == 1);

    B bNode = getInstance((A) bN);
    assertTrue(bNode.getChildren() != null);
    assertTrue(bNode.getChildren().size() == 1);
    assertTrue(bNode.getChildren().contains(bN));
  }

  // /**
  // * Test isDouble.
  // */
  // public void testIsDouble () {
  // ValueNode<Boolean> bN = getInstance (true);
  // assertFalse (bN.isDouble());
  //
  // ValueNode<Double> bD = getInstance (2.3);
  // assertTrue (bD.isDouble());
  // }
  //
  @SuppressWarnings("unchecked")
  @Override
  public void testClone() {
    B n1 = getInstance(getA(0));
    B n2 = null;
    try {
      n2 = (B) n1.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(n1 != n2);

    B n3 = getInstance((A) n1);
    B n4 = null;

    try {
      n4 = (B) n3.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(n3 != n4);
    // assertTrue (n3.getValue() != n4.getValue());
  }

}
