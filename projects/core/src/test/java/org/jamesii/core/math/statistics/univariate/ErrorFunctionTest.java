/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import static org.jamesii.core.math.statistics.univariate.ErrorFunction.*;

import org.jamesii.core.math.statistics.univariate.ErrorFunction;

import junit.framework.TestCase;

/** Tests the {@link ErrorFunction} class. */
public class ErrorFunctionTest extends TestCase {

  /** Data points for testing. */
  double[] dataPoints = { -5, 0, 1, -0.1, 0.1, 3, -3, 10, 5, 10, -20, -35, 35,
      Double.MIN_VALUE, Double.MAX_VALUE, Math.PI };

  /** Tests the {@link ErrorFunction#erf(double)} method. */
  public void testErf() {
    // some selected values
    assertEquals(0.0, erf(0), 0.00000001);
    assertEquals(-1.0, erf(Double.NEGATIVE_INFINITY), 0.00000001);
    assertEquals(1.0, erf(Double.POSITIVE_INFINITY), 0.00000001);
    assertEquals(0.5204999, erf(.5), 0.0000001);
    assertEquals(0.7111556, erf(.75), 0.0000001);
    assertEquals(0.8427008, erf(1), 0.0000001);
    assertEquals(0.9661051, erf(1.5), 0.0000001);
    assertEquals(0.9953223, erf(2), 0.0000001);

    // ensure that the function is monotonous and upwards sloping
    double last = -1;
    double current;
    for (double x = -3; x < 3; x += .1) {
      current = erf(x);
      assertTrue(last + " was not less than " + current, last < current);
      last = current;
    }
  }

  /** Tests the {@link ErrorFunction#erfc(double)} method. */
  public void testErfc() {
    // some selected values
    assertEquals(1.0, erfc(0), 0.00000001);
    assertEquals(2.0, erfc(Double.NEGATIVE_INFINITY), 0.00000001);
    assertEquals(0.0, erfc(Double.POSITIVE_INFINITY), 0.00000001);
    assertEquals(0.8320040, erfc(.15), 0.0000001);
    assertEquals(0.3961439, erfc(.6), 0.0000001);
    assertEquals(0.1791092, erfc(.95), 0.0000001);
    assertEquals(0.0109095, erfc(1.8), 0.0000001);
    assertEquals(0.0000015, erfc(3.4), 0.0000001);

    // ensure that the function is monotonous and downwards sloping
    double last = 2;
    double current;
    for (double x = -3; x < 3; x += .1) {
      current = erfc(x);
      assertTrue(last + " was not greater than " + current, last > current);
      last = current;
    }
  }

  /**
   * Tests the {@link ErrorFunction#inverfc(double)} function.
   */
  public void testInverted() {
    double x = 0.3;
    assertTrue(Math.abs(x - inverf(erf(x))) < 0.001);
    assertTrue(Math.abs(x - inverfc(erfc(x))) < 0.001);
  }

  /**
   * Tests fundamental properties that must hold true for <em>all</em> values.
   */
  public void testIdentities() {
    for (double x : dataPoints) {
      // uneven function
      assertEquals(erf(-x), -erf(x), 0.0000001);
      // complementary
      assertEquals(erf(x), 1 - erfc(x), 0.0000001);
    }
  }

}
