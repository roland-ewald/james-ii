/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.complex;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import org.jamesii.core.math.complex.Complex;
import org.jamesii.core.math.complex.ComplexMath;

import junit.framework.TestCase;

/** Tests the {@link Complex} class. */
public class TestComplex extends TestCase {

  // Helper functions

  /**
   * Helper method for performing various assertions on the given data which are
   * needed pretty much all the time.
   * 
   * @param c
   *          A complex number to test.
   * @param re
   *          Reference for the real part.
   * @param im
   *          Reference for the imaginary part.
   * @param r
   *          Reference for the number's modulus.
   * @param phi
   *          Reference for the number's argument.
   */
  private void checkFields(Complex c, double re, double im, double r, double phi) {
    final double EPSILON = 1e-7;
    assertEquals("Real part is wrong. Expected " + im + ", got " + c.getReal()
        + ".", re, c.getReal(), EPSILON);
    assertEquals(
        "Imaginary  part is wrong. Expected " + im + ", got "
            + c.getImaginary() + ".", im, c.getImaginary(), EPSILON);
    assertEquals(
        "Absolute value is wrong. Expected " + r + ", got " + c.getModulus()
            + ".", r, c.getModulus(), EPSILON);
    assertEquals("Angle is wrong. Expected " + phi + ", got " + c.getArgument()
        + ".", phi, c.getArgument(), EPSILON);
  }

  /**
   * Checks whether two complex numbers are approximately equal. Since all
   * calculations are done with {@code double}s it can (and does) happen that
   * results of calculations are not exact, thus the need for less-than-exact
   * checks.
   * 
   * @param a
   *          The first complex number to compare.
   * @param b
   *          The second complex number to compare.
   * @return {@code true} if both numbers are approximately equal, {@code false}
   *         if not.
   */
  private boolean approximatelyEqual(Complex a, Complex b) {
    final double EPSILON = 1e-10;
    return (abs(a.getReal() - b.getReal()) < EPSILON)
        && (abs(a.getImaginary() - b.getImaginary()) < EPSILON)
        || (abs(a.getModulus() - b.getModulus()) < EPSILON)
        && (abs(a.getArgument() - b.getArgument()) < EPSILON);
  }

  /** Tests the {@link Complex#Complex()} constructor. */
  public void testEmptyConstructor() {
    checkFields(new Complex(), 0, 0, 0, 0);
  }

  /** Tests the {@link Complex#Complex(double)} constructor. */
  public void testRealConstructor() {
    checkFields(new Complex(5), 5, 0, 5, 0);
    checkFields(new Complex(-2), -2, 0, 2, PI);
    checkFields(new Complex(0), 0, 0, 0, 0);
  }

  /** Tests the {@link Complex#Complex(double, double)} constructor. */
  public void testRealImaginaryConstructor() {
    checkFields(new Complex(1, 1), 1, 1, sqrt(2), .25 * PI);
    checkFields(new Complex(0, 4), 0, 4, 4, .5 * PI);
    checkFields(new Complex(-7, 0), -7, 0, 7, PI);
    checkFields(new Complex(0, 0), 0, 0, 0, 0);
  }

  /** Tests the static {@link Complex#fromPolar(double, double)} method. */
  public void testFromPolar() {
    Complex c = Complex.fromPolar(5, 0);
    checkFields(c, 5, 0, 5, 0);
    c = Complex.fromPolar(4, PI / 2);
    checkFields(c, 0, 4, 4, PI / 2);
    c = Complex.fromPolar(sqrt(2), PI / 4);
    checkFields(c, 1, 1, sqrt(2), PI / 4);
  }

  /** Tests the static {@link Complex#fromCartesian(double, double)} method. */
  public void testFromCartesian() {
    checkFields(Complex.fromCartesian(1, 1), 1, 1, sqrt(2), .25 * PI);
    checkFields(Complex.fromCartesian(0, 4), 0, 4, 4, .5 * PI);
    checkFields(Complex.fromCartesian(-7, 0), -7, 0, 7, PI);
    checkFields(Complex.fromCartesian(0, 0), 0, 0, 0, 0);
  }

  /**
   * Tests whether the number's argument is correctly normalised to a value in
   * the range of −<i>π</i> to +<i>π</i>
   */
  public void testNormalizePhi() {
    checkFields(Complex.fromPolar(5, 100), 4.311594361, -2.531828206, 5,
        -0.5309649149);
  }

  /** Tests the {@link Complex#negate()} method. */
  public void testNegate() {
    checkFields(new Complex(1, 2).negate(), -1, -2, sqrt(5), -2.034443936);
  }

  /** Tests the {@link Complex#conjugate()} method. */
  public void testConjugate() {
    checkFields(new Complex(1, 1).conjugate(), 1, -1, sqrt(2), -.25 * PI);
  }

  /** Tests the {@link Complex#add(Complex)} method. */
  public void testAdd() {
    checkFields(new Complex(1, 1).add(new Complex(7, -4)), 8, -3, sqrt(73),
        -0.3587706703);
  }

  /** Tests the {@link Complex#subtract(Complex)} method. */
  public void testSubtract() {
    checkFields(new Complex(1, 1).subtract(new Complex(5, -4)), -4, 5,
        sqrt(41), 2.245537269);
  }

  /** Tests the {@link Complex#multiply(double)} method. */
  public void testMultiplyReal() {
    checkFields(new Complex(1, 4).multiply(.1), .1, .4, sqrt(17) / 10,
        1.325817664);
    checkFields(new Complex(1.2, 5).multiply(8.4), 252d / 25, 42, 43.19266604,
        1.335251346);
  }

  /** Tests the {@link Complex#multiply(Complex)} method. */
  public void testMultiplyComplex() {
    checkFields(new Complex(7, 5).multiply(new Complex(-2, 8)), -54, 46,
        70.9365914, 2.436024476);
    checkFields(new Complex(1, 2).multiply(new Complex(0, 0)), 0, 0, 0, 0);
    checkFields(new Complex(1.2, -5).multiply(new Complex(-0.1, -4)),
        -503d / 25, -43d / 10, 20.57436269, -2.931042466);
  }

  /** Tests the {@link Complex#divide(double)} method. */
  public void testDivideReal() {
    checkFields(new Complex(1, 2).divide(2), .5, 1, 1.118033989, 1.107148718);
  }

  /** Tests the {@link Complex#divide(Complex)} method. */
  public void testDivideComplex() {
    checkFields(new Complex(5, 4).divide(new Complex(-1, 2)), 3d / 5, -14d / 5,
        2.863564213, -1.359702994);
  }

  /** Tests the {@link Complex#isReal()} method. */
  public void testIsReal() {
    assertTrue(new Complex(1, 0).isReal());
    assertFalse(new Complex(0, 1).isReal());
    assertFalse(new Complex(1, 1).isReal());
  }

  /** Tests the {@link Complex#isImaginary()} method. */
  public void testIsImaginary() {
    assertTrue(new Complex(0, 1).isImaginary());
    assertFalse(new Complex(1, 0).isImaginary());
    assertFalse(new Complex(1, 1).isImaginary());
  }

  /**
   * Checks for various properties of calculations with complex numbers. Those
   * must hold for all complex numbers.
   * 
   * @param a
   *          A complex number.
   * @param b
   *          A second complex number.
   */
  private void checkProperties(Complex a, Complex b) {
    // tests for absolute value
    if (a.equals(new Complex(0, 0))) {
      assertTrue(a.getModulus() == 0);
    }
    if (a.getModulus() == 0) {
      assertTrue(a.equals(new Complex(0, 0)));
    }
    assertTrue(a.add(b).getModulus() <= a.getModulus() + b.getModulus());
    assertTrue(Double.compare(a.multiply(b).getModulus(),
        a.getModulus() * b.getModulus()) == 0);

    // complex conjugates
    assertTrue(approximatelyEqual(a.add(b).conjugate(),
        a.conjugate().add(b.conjugate())));
    assertTrue(approximatelyEqual(a.multiply(b).conjugate(), a.conjugate()
        .multiply(b.conjugate())));
    if (!b.equals(new Complex(0, 0))) {
      assertTrue(approximatelyEqual(a.divide(b).conjugate(), a.conjugate()
          .divide(b.conjugate())));
    }
    assertTrue(a.conjugate().conjugate().equals(a));
    if (a.isReal()) {
      assertTrue(a.conjugate().equals(a));
    }
    if (a.conjugate().equals(a)) {
      assertTrue(a.isReal());
    }
    if (a.isImaginary()) {
      assertTrue(a.conjugate().equals(a.negate()));
    }
    if (a.conjugate().equals(a.negate())) {
      assertTrue(a.isImaginary());
    }
    assertTrue(a.getModulus() == a.conjugate().getModulus());
    assertTrue(a.multiply(a.conjugate()).isReal());
    assertTrue(a.multiply(a.conjugate()).getReal() - pow(a.getModulus(), 2) < 1e-10);
    if (!a.equals(new Complex(0, 0))) {
      assertTrue(approximatelyEqual(ComplexMath.pow(a, -1), a.conjugate()
          .multiply(pow(a.getModulus(), -2))));
    }
  }

  /**
   * Tests various calculations with complex numbers which must hold true for
   * all.
   */
  public void testProperties() {
    // Tests various properties of calculations with complex numbers. Some
    // special cases are tested here.

    // both numbers are 0.
    checkProperties(new Complex(0, 0), new Complex(0, 0));
    // one number is zero, the other an arbitrary real number
    checkProperties(new Complex(15, 0), new Complex(0, 0));
    checkProperties(new Complex(0, 0), new Complex(2, 0));
    // one number is zero, the other an arbitrary imaginary number
    checkProperties(new Complex(0, 0), new Complex(0, 3));
    checkProperties(new Complex(0, 5), new Complex(0, 0));
    // one number is zero, the other an arbitrary complex number
    checkProperties(new Complex(0, 0), new Complex(2, 6));
    checkProperties(new Complex(8, 13), new Complex(0, 0));
    // one number is one, the other zero
    checkProperties(new Complex(1, 0), new Complex(0, 0));
    checkProperties(new Complex(0, 0), new Complex(1, 0));
    // both numbers are one
    checkProperties(new Complex(1, 0), new Complex(1, 0));
    checkProperties(new Complex(1, 0), new Complex(1, 0));
    // one number is one, the other an arbitrary real number
    checkProperties(new Complex(1, 0), new Complex(46, 0));
    checkProperties(new Complex(-98, 0), new Complex(1, 0));
    // one number is one, the other an arbitrary imaginary number
    checkProperties(new Complex(1, 0), new Complex(0, 211));
    checkProperties(new Complex(0, -32), new Complex(1, 0));
    // one number is one, the other an arbitrary complex number
    checkProperties(new Complex(1, 0), new Complex(2, -7));
    checkProperties(new Complex(-1, 5), new Complex(1, 0));
    // one number is i, the other zero
    checkProperties(new Complex(0, 1), new Complex(0, 0));
    checkProperties(new Complex(0, 0), new Complex(0, 1));
    // both numbers are i
    checkProperties(new Complex(0, 1), new Complex(0, 1));
    checkProperties(new Complex(0, 1), new Complex(0, 1));
    // one number is i, the other an arbitrary real number
    checkProperties(new Complex(0, 1), new Complex(42, 0));
    checkProperties(new Complex(-23, 0), new Complex(0, 1));
    // one number is i, the other an arbitrary imaginary number
    checkProperties(new Complex(0, 1), new Complex(0, 15));
    checkProperties(new Complex(0, -8), new Complex(0, 1));
    // one number is i, the other an arbitrary complex number
    checkProperties(new Complex(0, 1), new Complex(-6, 48));
    checkProperties(new Complex(19, -1), new Complex(0, 1));
  }

  /** Tests the {@link Complex#equals(Object)} method. */
  public void testEquals() {
    assertTrue(new Complex(1, 1).equals(new Complex(1, 1)));
    assertFalse(new Complex(1, 1).equals(null));
    assertFalse(new Complex(1, 1).equals("blah"));
    assertFalse(new Complex(1, 1).equals(new Complex(2, 2)));
  }

  /** Tests the {@link Complex#hashCode()} method. */
  public void testHashCode() {
    Complex a = new Complex(1, 1);
    Complex b = new Complex(1, 0);
    b = b.add(new Complex(0, 1));
    assertTrue(a.hashCode() == a.hashCode());
    assertTrue(a.hashCode() == b.hashCode());
  }

  /** Tests the {@link Complex#toString()} method. */
  public void testToString() {
    assertTrue(new Complex(0, 0).toString().equals("0"));
    assertTrue(new Complex(1, 1).toString().equals("1.0 + 1.0i"));
    assertTrue(new Complex(-5, 1).toString().equals("-5.0 + 1.0i"));
    assertTrue(new Complex(-5, -2).toString().equals("-5.0 - 2.0i"));
    assertTrue(new Complex(-5, 0).toString().equals("-5.0"));
    assertTrue(new Complex(0, 1).toString().equals("1.0i"));
    assertTrue(new Complex(0, -7.5).toString().equals("-7.5i"));
  }

  /** Tests the {@link Complex#doubleValue()} method. */
  public void testDoubleValue() {
    Complex c = new Complex(Math.random() * 100, Math.random() * 100);
    assertEquals(c.doubleValue(), c.getReal());
  }

  /** Tests the {@link Complex#floatValue()} method. */
  public void testFloatValue() {
    Complex c = new Complex(Math.random() * 100, Math.random() * 100);
    assertEquals(c.floatValue(), (float) c.getReal());
  }

  /** Tests the {@link Complex#intValue()} method. */
  public void testIntValue() {
    Complex c = new Complex(Math.random() * 100, Math.random() * 100);
    assertEquals(c.intValue(), (int) c.getReal());
  }

  /** Tests the {@link Complex#longValue()} method. */
  public void testLongValue() {
    Complex c = new Complex(Math.random() * 100, Math.random() * 100);
    assertEquals(c.longValue(), (long) c.getReal());
  }

  /** Tests the {@link Complex#byteValue()} method. */
  public void testByteValue() {
    Complex c = new Complex(Math.random() * 100, Math.random() * 100);
    assertEquals(c.byteValue(), (byte) c.getReal());
  }

  /** Tests the {@link Complex#shortValue()} method. */
  public void testShortValue() {
    Complex c = new Complex(Math.random() * 100, Math.random() * 100);
    assertEquals(c.shortValue(), (short) c.getReal());
  }
}
