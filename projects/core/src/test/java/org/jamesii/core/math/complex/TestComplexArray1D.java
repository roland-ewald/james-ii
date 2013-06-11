/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.complex;

import org.jamesii.core.math.complex.Complex;
import org.jamesii.core.math.complex.ComplexArray1D;

import junit.framework.TestCase;

/**
 * The Class TestComplexArray1D.
 */
public class TestComplexArray1D extends TestCase {

  /**
   * Test empty constructor.
   */
  public void testEmptyConstructor() {
    ComplexArray1D a = new ComplexArray1D();
    assertTrue(a.getLength() == 0);
  }

  /**
   * Test length constructor exceptions.
   */
  public void testLengthConstructorExceptions() {
    try {
      new ComplexArray1D(-1);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Test length constructor.
   */
  public void testLengthConstructor() {
    ComplexArray1D a = new ComplexArray1D(5);

    assertTrue(a.getLength() == 5);

    // check for numbers being zero
    for (int i = 0; i < a.getLength(); i++) {
      assertTrue(a.getComplex(i).equals(new Complex(0, 0)));
    }
  }

  /**
   * Test complex array constructor.
   */
  public void testComplexArrayConstructor() {
    Complex[] x =
        new Complex[] { new Complex(1, 2), new Complex(3, 4),
            new Complex(8, 7), new Complex(1.5, 5.75), new Complex(-8, -5.4) };
    ComplexArray1D a = new ComplexArray1D(x);

    assertTrue(x.length == a.getLength());

    for (int i = 0; i < a.getLength(); i++) {
      assertTrue(x[i].equals(a.getComplex(i)));
    }
  }

  /**
   * Test put get complex.
   */
  public void testPutGetComplex() {
    ComplexArray1D a = new ComplexArray1D(5);

    // test put and get
    a.putComplex(2, -24, 42);
    a.putComplex(0, new Complex(1, 2));
    a.putComplex(3, 10, 12);
    a.putComplex(1, new Complex(5, -7));

    // test exceptions
    try {
      a.putComplex(-1, new Complex(8, 18));
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.putComplex(34436, new Complex(8, 18));
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.getComplex(-4);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.getComplex(123);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // if nothing interfered in between all values should be correct
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(5, -7)));
    assertTrue(a.getComplex(2).equals(new Complex(-24, 42)));
    assertTrue(a.getComplex(3).equals(new Complex(10, 12)));
    assertTrue(a.getComplex(4).equals(new Complex(0, 0)));
  }

  /**
   * Test set length.
   */
  public void testSetLength() {
    ComplexArray1D a =
        new ComplexArray1D(new Complex[] { new Complex(1, 2),
            new Complex(3, 4), new Complex(5, 6) });

    assertTrue(a.getLength() == 3);
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    a.setLength(3);

    assertTrue(a.getLength() == 3);
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    a.setLength(2);

    assertTrue(a.getLength() == 2);
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));

    a.setLength(3);

    assertTrue(a.getLength() == 3);
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(0, 0)));
  }

  /**
   * Test set length exceptions.
   */
  public void testSetLengthExceptions() {
    ComplexArray1D a =
        new ComplexArray1D(new Complex[] { new Complex(1, 2),
            new Complex(3, 4), new Complex(5, 6) });

    try {
      a.setLength(-1);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Test negate exceptions.
   */
  public void testNegateExceptions() {
    ComplexArray1D a = new ComplexArray1D(new Complex[] { new Complex(1, 1) });

    try {
      a.negate(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.negate(5);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // check for side-effects. Nothing should have changed!
    assertTrue(a.getComplex(0).equals(new Complex(1, 1)));
  }

  /**
   * Test negate.
   */
  public void testNegate() {
    ComplexArray1D a =
        new ComplexArray1D(new Complex[] { new Complex(1, 4),
            new Complex(-2, 3), new Complex(-1, -3), new Complex() });

    ComplexArray1D b = new ComplexArray1D(a.toComplexArray());

    for (int i = 0; i < a.getLength(); i++) {
      a.negate(i);

      // check for side-effects in other numbers of the array -- there shouldn't
      // be any
      for (int j = 0; j < a.getLength(); j++) {
        if (i != j) {
          assertTrue(a.getComplex(j) + " was not equal to " + b.getComplex(j),
              a.getComplex(j).equals(b.getComplex(j)));
        } else {
          assertTrue(a.getComplex(j).equals(b.getComplex(j).negate()));
        }
      }

      // since the operation is the reverse of itself, this should revert
      // everything back to the state before
      a.negate(i);
      assertTrue(a.getComplex(i).equals(b.getComplex(i)));
    }
  }

  /**
   * Test conjugate exceptions.
   */
  public void testConjugateExceptions() {
    ComplexArray1D a = new ComplexArray1D(new Complex[] { new Complex(1, 1) });

    try {
      a.conjugate(-1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.conjugate(5);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // check for side-effects. Nothing should have changed!
    assertTrue(a.getComplex(0).equals(new Complex(1, 1)));
  }

  /**
   * Test conjugate.
   */
  public void testConjugate() {
    ComplexArray1D a =
        new ComplexArray1D(new Complex[] { new Complex(1, 4),
            new Complex(-2, 3), new Complex(-1, -3), new Complex() });

    ComplexArray1D b = new ComplexArray1D(a.toComplexArray());

    for (int i = 0; i < a.getLength(); i++) {
      a.conjugate(i);

      // check for side-effects in other numbers of the array -- there shouldn't
      // be any
      for (int j = 0; j < a.getLength(); j++) {
        if (i != j) {
          assertTrue(a.getComplex(j) + " was not equal to " + b.getComplex(j),
              a.getComplex(j).equals(b.getComplex(j)));
        } else {
          assertTrue(a.getComplex(j).equals(b.getComplex(j).conjugate()));
        }
      }

      // since the operation is the reverse of itself, this should revert
      // everything back to the state before
      a.conjugate(i);
      assertTrue(a.getComplex(i).equals(b.getComplex(i)));
    }
  }

  /**
   * Test add exceptions.
   */
  public void testAddExceptions() {
    ComplexArray1D a = new ComplexArray1D(new Complex[] { new Complex(1, 1) });

    // testing add(int, double, double, double, double)
    try {
      a.add(-1, 2d, 3d, 1d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(5, 5d, 4d, 2d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing add(int, int, double, double)
    try {
      a.add(-1, 0, 1d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(5, 0, 1d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, -2, 2d, 3d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, 10, 3d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing add(int, int, int)
    try {
      a.add(-1, 0, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(5, 0, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, -2, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, 10, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, 0, -1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, 0, 10);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // check for side-effects. Nothing should have changed!
    assertTrue(a.getComplex(0).equals(new Complex(1, 1)));
  }

  /**
   * Test add.
   */
  public void testAdd() {
    ComplexArray1D a =
        new ComplexArray1D(new Complex[] { new Complex(1, 2),
            new Complex(3, 4), new Complex(5, 6) });

    // initial tests ... we can't have too many of them :)
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing add(int, int, int)
    a.add(0, 1, 2);
    assertTrue(a.getComplex(0).equals(new Complex(8, 10)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing add(int, int, double, double)
    a.add(0, 1, 1.5, -4.75);
    assertTrue(a.getComplex(0).equals(new Complex(4.5, -.75)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing add(int, double, double, double, double)
    a.add(2, 4, 2, -1, -8);
    assertTrue(a.getComplex(0).equals(new Complex(4.5, -.75)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(3, -6)));
  }

  /**
   * Test subtract exceptions.
   */
  public void testSubtractExceptions() {
    ComplexArray1D a = new ComplexArray1D(new Complex[] { new Complex(1, 1) });

    // testing subtract(int, double, double, double, double)
    try {
      a.subtract(-1, 3d, 4d, 1d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.subtract(5, 2d, 3d, 2d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing subtract(int, int, double, double)
    try {
      a.subtract(-1, 0, 1d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.subtract(5, 0, 1d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.subtract(0, -2, 1d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.subtract(0, 10, 1d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing subtract(int, double, double, int)
    try {
      a.subtract(-1, 1d, 1d, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.subtract(5, 1d, 1d, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.subtract(0, 1d, 1d, -2);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.subtract(0, 1d, 1d, 10);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing subtract(int, int, int)
    try {
      a.subtract(-1, 0, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(5, 0, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, -2, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, 10, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, 0, -1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.add(0, 0, 10);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // check for side-effects. Nothing should have changed!
    assertTrue(a.getComplex(0).equals(new Complex(1, 1)));
  }

  /**
   * Test subtract.
   */
  public void testSubtract() {
    ComplexArray1D a =
        new ComplexArray1D(new Complex[] { new Complex(1, 2),
            new Complex(3, 4), new Complex(5, 6) });

    // initial tests ... we can't have too many of them :)
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing subtract(int, int, int)
    a.subtract(0, 1, 2);
    assertTrue(a.getComplex(0).equals(new Complex(-2, -2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing subtract(int, int, double, double)
    a.subtract(0, 1, 1.5, -4.75);
    assertTrue(a.getComplex(0).equals(new Complex(1.5, 8.75)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing subtract(int, double, double, int)
    a.subtract(0, 1.5, -4.75, 2);
    assertTrue(a.getComplex(0).equals(new Complex(-3.5, -10.75)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing subtract(int, double, double, double, double)
    a.subtract(2, 4, 2, -1, -8);
    assertTrue(a.getComplex(0).equals(new Complex(-3.5, -10.75)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 10)));
  }

  /**
   * Test multiply exceptions.
   */
  public void testMultiplyExceptions() {
    ComplexArray1D a = new ComplexArray1D(new Complex[] { new Complex(1, 1) });

    // testing multiply(int, double, double, double, double)
    try {
      a.multiply(-1, 2d, 3d, 1d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(5, 5d, 4d, 2d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing multiply(int, int, double, double)
    try {
      a.multiply(-1, 0, 1d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(5, 0, 1d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(0, -2, 2d, 3d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(0, 10, 3d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing multiply(int, int, int)
    try {
      a.multiply(-1, 0, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(5, 0, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(0, -2, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(0, 10, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(0, 0, -1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.multiply(0, 0, 10);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // check for side-effects. Nothing should have changed!
    assertTrue(a.getComplex(0).equals(new Complex(1, 1)));
  }

  /**
   * Test multiply.
   */
  public void testMultiply() {
    ComplexArray1D a =
        new ComplexArray1D(new Complex[] { new Complex(1, 2),
            new Complex(3, 4), new Complex(5, 6) });

    // initial tests ... we can't have too many of them :)
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing multiply(int, int, int)
    a.multiply(0, 1, 2);
    assertTrue(a.getComplex(0).equals(new Complex(-9, 38)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing multiply(int, int, double, double)
    a.multiply(0, 1, 1.5, -4.75);
    assertTrue(a.getComplex(0).equals(new Complex(23.5, -8.25)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing multiply(int, double, double, double, double)
    a.multiply(2, 4, 2, -1, -8);
    assertTrue(a.getComplex(0).equals(new Complex(23.5, -8.25)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(12, -34)));
  }

  /**
   * Test divide exceptions.
   */
  public void testDivideExceptions() {
    ComplexArray1D a = new ComplexArray1D(new Complex[] { new Complex(1, 1) });

    // testing divide(int, double, double, double, double)
    try {
      a.divide(-1, 2d, 3d, 1d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(5, 5d, 4d, 2d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing divide(int, int, double, double)
    try {
      a.divide(-1, 0, 1d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(5, 0, 1d, 1d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(0, -2, 2d, 3d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(0, 10, 3d, 2d);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // testing divide(int, int, int)
    try {
      a.divide(-1, 0, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(5, 0, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(0, -2, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(0, 10, 0);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(0, 0, -1);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      a.divide(0, 0, 10);
      fail();
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }

    // check for side-effects. Nothing should have changed!
    assertTrue(a.getComplex(0).equals(new Complex(1, 1)));
  }

  /**
   * Test divide.
   */
  public void testDivide() {
    ComplexArray1D a =
        new ComplexArray1D(new Complex[] { new Complex(1, 2),
            new Complex(3, 4), new Complex(5, 6) });

    // initial tests ... we can't have too many of them :)
    assertTrue(a.getComplex(0).equals(new Complex(1, 2)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing divide(int, int, int)
    a.divide(0, 1, 2);
    assertTrue(a.getComplex(0).equals(new Complex(39.0 / 61, 2.0 / 61)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing divide(int, int, double, double)
    a.divide(0, 1, 1.5, -4.75);
    assertTrue(a.getComplex(0).equals(new Complex(-232d / 397, 324d / 397)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(5, 6)));

    // testing divide(int, double, double, double, double)
    a.divide(2, 4.0, 2.0, -1.0, -8.0);
    assertTrue(a.getComplex(0).equals(new Complex(-232d / 397, 324d / 397)));
    assertTrue(a.getComplex(1).equals(new Complex(3, 4)));
    assertTrue(a.getComplex(2).equals(new Complex(-4d / 13, 6d / 13)));

    // testing divide(int, double, double, int)
    a.divide(1, 1.0, 1.0, 2);
    assertTrue(a.getComplex(0).equals(new Complex(-232d / 397, 324d / 397)));
    System.out.println(a.getComplex(1));
    assertTrue(a.getComplex(1).equals(new Complex(1d / 2, -5d / 2)));
    assertTrue(a.getComplex(2).equals(new Complex(-4d / 13, 6d / 13)));
  }

  /**
   * Test to complex array.
   */
  public void testToComplexArray() {
    Complex[] a =
        new Complex[] { new Complex(1, 2), new Complex(3, 4), new Complex(5, 6) };
    ComplexArray1D b = new ComplexArray1D(a);
    Complex[] c = b.toComplexArray();

    for (int i = 0; i < a.length; i++) {
      assertTrue(a[i].equals(c[i]));
    }
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestComplexArray1D.class);
  }

}
