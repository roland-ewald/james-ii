/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.complex;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

import org.jamesii.core.math.complex.Complex;
import org.jamesii.core.math.complex.ComplexMath;

import junit.framework.TestCase;

/**
 * The Class TestComplexMath.
 */
public class TestComplexMath extends TestCase {

  /**
   * Check fields.
   * 
   * @param c
   *          the c
   * @param re
   *          the re
   * @param im
   *          the im
   * @param r
   *          the r
   * @param phi
   *          the phi
   */
  private void checkFields(Complex c, double re, double im, double r, double phi) {
    final double EPSILON = 1e-8;

    assertTrue("Real part is wrong. Expected " + re + ", got " + c.getReal()
        + ".", abs(c.getReal() - re) < EPSILON);
    assertTrue(
        "Imaginary  part is wrong. Expected " + im + ", got "
            + c.getImaginary() + ".", abs(c.getImaginary() - im) < EPSILON);
    assertTrue(
        "Absolute value is wrong. Expected " + r + ", got " + c.getModulus()
            + ".", abs(c.getModulus() - r) < EPSILON);
    assertTrue("Angle is wrong. Expected " + phi + ", got " + c.getArgument()
        + ".", abs(c.getArgument() - phi) < EPSILON);
  }

  /**
   * Approximately equal.
   * 
   * @param a
   *          the a
   * @param b
   *          the b
   * 
   * @return true, if successful
   */
  private boolean approximatelyEqual(Complex a, Complex b) {
    final double EPSILON = 1e-10;
    return (abs(a.getReal() - b.getReal()) < EPSILON)
        && (abs(a.getImaginary() - b.getImaginary()) < EPSILON);
  }

  /**
   * Test abs.
   */
  public void testAbs() {
    // just for good measure -- getModulus is already tested elsewhere
    assertTrue(ComplexMath.abs(new Complex(12, 34)) == new Complex(12, 34)
        .getModulus());
  }

  /**
   * Test power real.
   */
  public void testPowerReal() {
    checkFields(ComplexMath.pow(new Complex(1, 2), 2), -3, 4, 5, 2.214297436);
    checkFields(ComplexMath.pow(new Complex(-1.5, 2.25), 2), -2.8125, -6.75,
        7.3125, -1.965587446);
    checkFields(ComplexMath.pow(new Complex(0, 1), 2), -1, 0, 1, PI);
    checkFields(ComplexMath.pow(new Complex(0, 1), 3), 0, -1, 1, -.5 * PI);
    checkFields(ComplexMath.pow(new Complex(0, 1), 4), 1, 0, 1, 0);
    checkFields(ComplexMath.pow(new Complex(1, 2), 4), -7, -24, 25,
        -1.854590436);
    checkFields(ComplexMath.pow(new Complex(-4.5, 3), 1.2), -7.557483862,
        .585242733, 7.580110249, 3.06430806);
  }

  /**
   * Test power complex.
   */
  public void testPowerComplex() {
    checkFields(ComplexMath.pow(new Complex(0, 1), new Complex(0, 1)),
        exp(-PI / 2), 0, exp(-PI / 2), 0);
    checkFields(ComplexMath.pow(new Complex(1, 1), new Complex(1, 1)),
        0.2739572538, 0.5837007588, sqrt(2) * exp(-PI / 4), .25 * PI + .5
            * log(2));
    // check whether power(double) as special case (with different computation)
    // yields the same results:
    Complex a = ComplexMath.pow(new Complex(1, 2), (4.5));
    Complex b = ComplexMath.pow(new Complex(1, 2), new Complex(4.5));
    checkFields(a, b.getReal(), b.getImaginary(), b.getModulus(),
        b.getArgument());
  }

  /**
   * Test sqrt.
   */
  public void testSqrt() {
    Complex[] x;

    // trivial case, square root of a positive real number
    x = ComplexMath.sqrt(new Complex(4, 0));
    assertTrue(x.length == 2);
    for (int i = 0; i < x.length; i++) {
      assertTrue(approximatelyEqual(x[i], new Complex(2, 0))
          || approximatelyEqual(x[i], new Complex(-2, 0)));
    }

    // square root of a negative real number
    x = ComplexMath.sqrt(new Complex(-9, 0));
    assertTrue(x.length == 2);
    for (int i = 0; i < x.length; i++) {
      assertTrue(approximatelyEqual(x[i], new Complex(0, 3))
          || approximatelyEqual(x[i], new Complex(0, -3)));
    }

    // square root of complex number
    x = ComplexMath.sqrt(new Complex(1, 2));
    assertTrue(x.length == 2);
    for (int i = 0; i < x.length; i++) {
      assertTrue(approximatelyEqual(x[i], new Complex(-1.2720196495140690,
          -0.7861513777574233))
          || approximatelyEqual(x[i], new Complex(1.2720196495140690,
              0.7861513777574233)));
    }
  }

  /**
   * Test cbrt.
   */
  public void testCbrt() {
    Complex[] x;

    // Trivial case, square root of a positive real number
    x = ComplexMath.cbrt(new Complex(8, 0));
    assertTrue(x.length == 3);
    for (int i = 0; i < x.length; i++) {
      assertTrue(approximatelyEqual(x[i], new Complex(-1, -1.7320508075688773))
          || approximatelyEqual(x[i], new Complex(-1, 1.7320508075688773))
          || approximatelyEqual(x[i], new Complex(2, 0)));
    }

    // Square root of a negative real number
    x = ComplexMath.cbrt(new Complex(-9, 0));
    assertTrue(x.length == 3);
    for (int i = 0; i < x.length; i++) {
      assertTrue(approximatelyEqual(x[i], new Complex(-2.0800838230519041, 0))
          || approximatelyEqual(x[i], new Complex(1.0400419115259521,
              -1.8014054327640041))
          || approximatelyEqual(x[i], new Complex(1.0400419115259521,
              1.8014054327640041)));
    }

    // Square root of complex number
    x = ComplexMath.cbrt(new Complex(8, -23));
    assertTrue(x.length == 3);
    for (int i = 0; i < x.length; i++) {
      assertTrue(approximatelyEqual(x[i], new Complex(-2.3332080247967582,
          -1.7197495217100161))
          || approximatelyEqual(x[i], new Complex(-0.3227427615486328,
              2.8804921826427131))
          || approximatelyEqual(x[i], new Complex(2.6559507863453910,
              -1.1607426609326970)));
    }
  }

  /**
   * Test root.
   */
  public void testRoot() {
    Complex[] reference, x;

    // Fifth root of a positive real number
    reference =
        new Complex[] {
            new Complex(-1.11622474376531575, -0.81098474715738870),
            new Complex(-1.11622474376531575, 0.81098474715738870),
            new Complex(0.42635991303470834, -1.31220088525839459),
            new Complex(0.42635991303470834, 1.31220088525839459),
            new Complex(1.3797296614612148) };
    x = ComplexMath.root(5, new Complex(5));
    assertTrue(x.length == 5);
    for (int i = 0; i < x.length; i++) {
      boolean temp = false;
      for (int j = 0; j < reference.length; j++) {
        temp |= approximatelyEqual(x[i], reference[j]);
      }
      assertTrue(temp);
    }

    // Eighth root of a negative real number
    reference =
        new Complex[] {
            new Complex(-1.84775906502257351, -0.76536686473017954),
            new Complex(-1.84775906502257351, 0.76536686473017954),
            new Complex(-0.76536686473017954, -1.84775906502257351),
            new Complex(-0.76536686473017954, 1.84775906502257351),
            new Complex(0.76536686473017954, -1.84775906502257351),
            new Complex(0.76536686473017954, 1.84775906502257351),
            new Complex(1.84775906502257351, -0.76536686473017954),
            new Complex(1.84775906502257351, 0.76536686473017954) };
    x = ComplexMath.root(8, new Complex(-256));
    assertTrue(x.length == 8);
    for (int i = 0; i < x.length; i++) {
      boolean temp = false;
      for (int j = 0; j < reference.length; j++) {
        temp |= approximatelyEqual(x[i], reference[j]);
      }
      assertTrue(temp);
    }

    // Fifth root of a complex number
    reference =
        new Complex[] { new Complex(-1.49595075945776575, 0.12140512225626765),
            new Complex(-0.57773734005399185, -1.38521747185763709),
            new Complex(-0.34681107478712560, 1.46024996382034682),
            new Complex(1.13888944673444278, -0.97751660167448831),
            new Complex(1.28160972756444042, 0.78107898745551094) };
    x = ComplexMath.root(5, new Complex(-7, 3));
    assertTrue(x.length == 5);
    for (int i = 0; i < x.length; i++) {
      boolean temp = false;
      for (int j = 0; j < reference.length; j++) {
        temp |= approximatelyEqual(x[i], reference[j]);
      }
      assertTrue(temp);
    }

    // negative square root of a complex number
    reference =
        new Complex[] { new Complex(-0.07285869092739988, 0.35496203179504731),
            new Complex(0.07285869092739988, -0.35496203179504731) };
    x = ComplexMath.root(-2, new Complex(-7, 3));
    assertTrue(x.length == 2);
    for (int i = 0; i < x.length; i++) {
      boolean temp = false;
      for (int j = 0; j < reference.length; j++) {
        temp |= approximatelyEqual(x[i], reference[j]);
      }
      assertTrue(temp);
    }

    /* Edge cases */

    // root(0, Complex) -- should yield no results.
    x = ComplexMath.root(0, new Complex(23, 42));
    assertTrue(x.length == 0);

    // root(0, 0) -- should yield no results.
    x = ComplexMath.root(0, new Complex(0, 0));
    assertTrue(x.length == 0);

    // root(3, 0) -- should yield zero, three times
    x = ComplexMath.root(3, new Complex(0, 0));
    assertTrue(x.length == 3);
    for (int i = 0; i < x.length; i++) {
      assertTrue(x[i].equals(new Complex(0, 0)));
    }

    // root(-2, 0) -- should yield no results.
    x = ComplexMath.root(-2, new Complex(0, 0));
    assertTrue(x.length == 0);
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestComplexMath.class);
  }

}
