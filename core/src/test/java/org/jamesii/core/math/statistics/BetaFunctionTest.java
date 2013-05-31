/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics;

import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.jamesii.core.math.statistics.BetaFunction.*;
import static org.jamesii.core.math.statistics.univariate.GammaFunction.*;

import org.jamesii.core.math.statistics.BetaFunction;

import junit.framework.TestCase;

/** Tests the {@link BetaFunction} class. */
public class BetaFunctionTest extends TestCase {

  /** Data points used for property testing. */
  private static final double[] dataPoints = { .1, .2, .3, .4, .5, .75, 1.0,
      1.5, 2.5, 3.0, PI, 9.999, 10, 10.001, 15, 30, 70, 70.1, -.1, -.2, -.3,
      -.4, -.5, -.75, -1.5, -1.001, -2.5, -5.99999, -15.2, -30.123, -65.789 };

  /**
   * Asserts that two double values are equal with the specified number of
   * significant figures. The {@link #assertEquals(double, double, double)}
   * method is useless since it only checks for digits after the decimal point,
   * which quickly become meaningless as tested numbers increase beyond
   * 10<sup>15</sup>.
   * 
   * @param expected
   *          The expected value.
   * @param actual
   *          The actual value to be tested.
   * @param significantFigures
   *          The number of figures that need to match.
   */
  private static void assertEquals(double expected, double actual,
      int significantFigures) {
    assertEquals(
        expected,
        actual,
        pow(10, -significantFigures)
            * pow(10, floor(log10(max(abs(expected), abs(actual))))));
  }

  /**
   * Tests special and selected function values of the Beta function (
   * {@link BetaFunction#beta}).
   */
  public void testBetaSpecialValues() {
    assertEquals(1, beta(1, 1), 14);
    assertEquals(0.5, beta(1, 2), 14);
    assertEquals(1.0 / 6.0, beta(2, 2), 14);
    assertEquals(0.28571428571428571429, beta(3.5, 1), 14);
    assertEquals(0.68198468198468198468, beta(7, 0.5), 14);
    assertEquals(0.067004422575263767822, beta(700, 0.5), 11);
    assertEquals(-19.617559825700598014, beta(-0.1, -0.1), 14);
    assertEquals(3107.4801466048192922, beta(-4.2, -7.9), 10);
    assertEquals(310.95624428835359458, beta(-2.2, -7.9), 11);
    assertEquals(-50.574465065555731898, beta(-2.2, -3.6), 12);
    assertEquals(7.1343282927688849020e-47, beta(76, 76), 14);
    assertEquals(109.83247503245110188, beta(.1, .01), 13);
    assertEquals(1.0000999836681719412e6, beta(.000001, .01), 7);
    assertEquals(1.0000000009998366979e11, beta(1e-11, .01), 2);
    assertEquals(9.999999999412696905e10, beta(1e-11, 200), 3);
    assertEquals(9.999999999999918221e14, beta(1e-15, 2000), 3);
    assertEquals(POSITIVE_INFINITY, beta(-2, -3.6));
    assertEquals(POSITIVE_INFINITY, beta(-1.2, -3));
  }

  /** Tests general properties of the Beta function ({@link BetaFunction#beta}). */
  public void testBetaProperties() {
    for (double a : dataPoints) {
      for (double b : dataPoints) {
        // symmetry
        assertEquals(beta(a, b), beta(b, a), 14);
        // Β(a, b) = Γ(a)Γ(b)/Γ(a+b)
        assertEquals(gamma(a) * gamma(b) / gamma(a + b), beta(a, b), 14);
      }
    }
  }

  /**
   * Tests whether the {@link BetaFunction#iBeta(double, double, double)} method
   * throws the correct exceptions when encountering invalid arguments.
   */
  public void testIBetaExceptions() {
    try {
      // x < 0
      iBeta(1, 1, -0.0001);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // x > 1
      iBeta(1, 1, 1.0001);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // a < 0
      iBeta(-0.5, 1, 0.5);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // a = 0
      iBeta(0, 1, 0.5);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // b < 0
      iBeta(1, -0.5, 0.5);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // b = 0
      iBeta(1, 0, 0.5);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }
  }

  /**
   * Tests special and selected function values of the incomplete Beta function
   * ({@link BetaFunction#iBeta}).
   */
  public void testIBetaSpecialValues() {
    assertEquals(0.76155802562198010512, iBeta(1.2, 4.7, 0.3), 15);
    assertEquals(0.94745391582682629692, iBeta(1.2, 4.7, 0.5), 15);
    assertEquals(0.99499127744968697709, iBeta(1.2, 4.7, 0.7), 15);
    assertEquals(9.9396193357895489203e-23, iBeta(15.75, 20.2, 0.01), 1e-33);
    assertEquals(0.99511636680046132405, iBeta(15.75, 20.2, 0.65), 14);
    // some test cases that provide more coverage
    assertEquals(0.00012755005351057953660, iBeta(120, 70, 0.5), 13);
    assertEquals(4.0846677319541029353e-31, iBeta(1200, 700, 0.5), 1e-42);
    assertEquals(4.0846677319541029353e-31, iBeta(1200, 700, 0.5), 1e-42);
    assertEquals(1.0, iBeta(0.5, 700, 0.5), 1e-15);
    assertEquals(0.5, iBeta(1e-30, 1e-30, 0.5), 1e-15);
    assertEquals(0.0, iBeta(4353, 982, 9.8e-4), 1e-15);
    assertEquals(1.0, iBeta(5233, 3472, 0.827), 1e-15);
    assertEquals(0.99639921713271979659, iBeta(4619, 6965, 0.411), 12);
    assertEquals(0.51843912960142920502, iBeta(1105, 1326, 0.455), 10);
  }

  /**
   * Tests general properties of the incomplete Beta function (
   * {@link BetaFunction#iBeta}).
   */
  public void testIBetaProperties() {
    for (double a : dataPoints) {
      if (a > 0) {
        for (double b : dataPoints) {
          if (b > 0) {
            assertEquals(0.0, iBeta(a, b, 0));
            assertEquals(1.0, iBeta(a, b, 1));
            for (double x = 0.1; x < 1; x += .01) {
              assertEquals(iBeta(a, b, x), 1 - iBeta(b, a, 1 - x), 1e-13);
            }
          }
        }
      }
    }
  }

}
