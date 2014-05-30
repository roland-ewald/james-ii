/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import org.jamesii.core.math.MathVector;
import org.jamesii.core.math.MatrixException;

import junit.framework.TestCase;

/**
 * The Class TestMathVector.
 */
public class TestMathVector extends TestCase {

  /**
   * Test constructor.
   */
  public void testConstructor() {
    MathVector v = new MathVector(0);
    assertEquals(0, v.getColumns());
    assertEquals(0, v.size());
    assertEquals(1, v.getRows());
    v = new MathVector(1);
    assertEquals(1, v.getColumns());
    assertEquals(1, v.size());
    assertEquals(1, v.getRows());
    assertEquals(0.0, v.getElement(0));
    v = new MathVector(2);
    assertEquals(2, v.getColumns());
    assertEquals(2, v.size());
    assertEquals(1, v.getRows());
    assertEquals(0.0, v.getElement(0));
    assertEquals(0.0, v.getElement(1));
    v = new MathVector(3);
    assertEquals(3, v.getColumns());
    assertEquals(3, v.size());
    assertEquals(1, v.getRows());
    assertEquals(0.0, v.getElement(0));
    assertEquals(0.0, v.getElement(1));
    assertEquals(0.0, v.getElement(2));
  }

  /**
   * Test constructor exceptions.
   */
  public void testConstructorExceptions() {
    try {
      new MathVector(-1);
      fail();
    } catch (NegativeArraySizeException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Test set element.
   */
  public void testSetElement() {
    MathVector v = new MathVector(2);

    v.setElement(0, 42.42);
    assertEquals(42.42, v.getElement(0));
    assertEquals(0.0, v.getElement(1));
    v.setElement(1, 23.23);
    assertEquals(42.42, v.getElement(0));
    assertEquals(23.23, v.getElement(1));
  }

  /**
   * Test set element exceptions.
   */
  public void testSetElementExceptions() {
    MathVector v = new MathVector(2);

    try {
      v.setElement(3, 1.2);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Test copy.
   */
  public void testCopy() {
    MathVector v1 = new MathVector(3);

    v1.setElement(0, 1.2);
    v1.setElement(1, 3.4);
    v1.setElement(1, 6.7);

    MathVector v2 = v1.copy();

    assertNotNull(v1);
    assertNotNull(v2);
    // copy() should return a copy, not the same object
    assertFalse(v1 == v2);

    assertEquals(v1.getElement(0), v2.getElement(0));
    assertEquals(v1.getElement(1), v2.getElement(1));
    assertEquals(v1.getElement(2), v2.getElement(2));
  }

  /**
   * Test mult with scalar.
   */
  public void testMultWithScalar() {
    MathVector v = new MathVector(2);

    v.setElement(0, 1.2);
    v.setElement(1, 3.4);

    assertEquals(1.2, v.getElement(0));
    assertEquals(3.4, v.getElement(1));

    v.multWithScalar(5);

    assertEquals(6.0, v.getElement(0));
    assertEquals(17.0, v.getElement(1));
  }

  /**
   * Test scalar product.
   */
  public void testScalarProduct() {
    MathVector v1 = new MathVector(2), v2 = new MathVector(2);
    v1.setElement(0, 1.25);
    v1.setElement(1, 3.75);
    v2.setElement(0, 8.25);
    v2.setElement(1, -4.5);
    assertEquals(-105d / 16, v1.scalarProduct(v2));
  }

  /**
   * Test scalar product exceptions.
   */
  public void testScalarProductExceptions() {
    try {
      new MathVector(2).scalarProduct(new MathVector(3));
      fail();
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Test vector product.
   */
  public void testVectorProduct() {
    MathVector v1 = new MathVector(3), v2 = new MathVector(3);
    v1.setElement(0, 1);
    v1.setElement(1, 2);
    v1.setElement(2, 4);
    v2.setElement(0, -8);
    v2.setElement(1, 5);
    v2.setElement(2, 1.5);

    MathVector v3 = v1.vectorProduct(v2);

    assertEquals(-17.0, v3.getElement(0));
    assertEquals(-33.5, v3.getElement(1));
    assertEquals(21.0, v3.getElement(2));
  }

  /**
   * Test vector product exceptions.
   */
  public void testVectorProductExceptions() {
    try {
      new MathVector(2).vectorProduct(new MathVector(3));
      fail();
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // The cross product is only defined in 3 and 7 dimensions. Thus 2 and 4
    // dimensions must yield an error!

    try {
      new MathVector(2).vectorProduct(new MathVector(2));
      assertTrue(
          "The cross-product isn't really defined in two-dimensional space. ",
          false);
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    try {
      new MathVector(4).vectorProduct(new MathVector(4));
      assertTrue("The cross-product isn't really defined for dimensions other"
          + "than three and seven.", false);
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }
}
