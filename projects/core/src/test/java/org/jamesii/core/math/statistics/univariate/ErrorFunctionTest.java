/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import static org.jamesii.core.math.statistics.univariate.ErrorFunction.erf;
import static org.jamesii.core.math.statistics.univariate.ErrorFunction.erfc;
import static org.jamesii.core.math.statistics.univariate.ErrorFunction.inverf;
import static org.jamesii.core.math.statistics.univariate.ErrorFunction.inverfc;
import junit.framework.TestCase;

/**
 * Tests the {@link ErrorFunction} class.
 * 
 * @author unascribed
 * @author Arne Bittig
 */
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
   * Test {@link ErrorFunction#inverf(double)} and
   * {@link ErrorFunction#inverfc(double)} functions.
   */
  public void testInverted() {
    double[] testvalues = { 0.001, 0.3, 0.5, 0.7 };
    for (int i = 0; i < testvalues.length; i++) {
      double x = testvalues[i];
      double inverfErfRes = inverf(erf(x));
      assertEquals(x, inverfErfRes, 0.001);
      double erfInverfRes = erf(inverf(x));
      assertEquals(x, erfInverfRes, 0.001);
      double inverfcErfcRes = inverfc(erfc(x));
      assertEquals(x, inverfcErfcRes, 0.001);
      // // too imprecise for low values:
      // double erfCInverfcRes = erfc(inverfc(x));
      // assertEquals(x,erfCInverfcRes, 0.001);
      System.out.print("inverf("+ x + ") = " + inverf(x));
      System.out.println("; inverf("+ (x+0.25) + ") = " + inverf(x+0.25,13));
    }
  }

  /**
   * Tests the precision of the {@link ErrorFunction#inverf(double,double)} and
   * {@link ErrorFunction#inverfc(double,double)} function.
   */
  public void testInvertedPrecision() {
    double[] testvalues = { 0.001, 0.3, 0.5, 0.7 };
    double[] precisions = { 0.00001, 0.0001, 0.001 };
    for (int i = 0; i < testvalues.length; i++) {
      for (int ip = 0; ip < precisions.length; ip++) {
        double x = testvalues[i];
        double prec = precisions[ip];
        double inverfErfRes = inverf(erf(x), prec);
        assertEquals(x, inverfErfRes, prec);
        double inverfcErfcRes = inverfc(erfc(x), prec);
        assertEquals(x, inverfcErfcRes, prec);
      }
    }
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
