/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static org.jamesii.core.math.statistics.IncompleteGammaFunction.*;
import static org.jamesii.core.math.statistics.univariate.ErrorFunction.*;
import static org.jamesii.core.math.statistics.univariate.GammaFunction.*;

import org.jamesii.core.math.statistics.IncompleteGammaFunction;

import junit.framework.TestCase;

/**
 * Tests the {@link IncompleteGammaFunction} class.
 * 
 * @author Johannes Rössel
 */
public class IncompleteGammaFunctionTest extends TestCase {

  /** Accuracy for the tests. */
  private static double EPSILON = 1e-13;

  /** Data points for testing. */
  double[] dataPoints = { 1.1, -0.1, 1.5, 3.2, -PI, PI, 0.9, 0.75, 0.5, -0.5,
      -0.75, 150.5, -150.5, 171.5, -169.1 };

  /**
   * Tests whether the correct exceptions are thrown when passing invalid
   * arguments to the {@link IncompleteGammaFunction#iGammaP(double, double)}
   * method.
   */
  public void testIGammaPExceptions() {
    try {
      // a negative
      iGammaP(-1, 1);
      fail();
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // x negative
      iGammaP(1, -1);
      fail();
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // a and x negative
      iGammaP(-1, -1);
      fail();
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }
  }

  /**
   * Tests special function values for the
   * {@link IncompleteGammaFunction#iGammaP(double, double)} method.
   */
  public void testIGammaPSpecialValues() {
    // some selected values
    assertEquals(0.022410702238350615, iGammaP(1.5, 0.1), EPSILON);
    assertEquals(0.9116235676432145, iGammaP(3, 5.5), EPSILON);
    assertEquals(0.987280192418805, iGammaP(3, 8.1), EPSILON);
    assertEquals(0.05424702615618924, iGammaP(1.2, 0.1), EPSILON);
    assertEquals(0.09020401043104986, iGammaP(2, 0.5), EPSILON);
    assertEquals(1.0, iGammaP(2, 90), EPSILON);
    assertEquals(0.9999969386042517, iGammaP(2, 15.5), EPSILON);
    assertEquals(0.0, iGammaP(2.16e305, 1.894e305));
  }

  /**
   * Tests general properties for the
   * {@link IncompleteGammaFunction#iGammaP(double, double)} method.
   */
  public void testIGammaPProperties() {
    for (double x : dataPoints) {
      if (x > 0) {
        // γ(1, x) = 1 - e^-x
        assertEquals(1 - exp(-x), iGammaP(1, x), EPSILON);
        // γ(1/2, x) = √π erf(√x)
        assertEquals(sqrt(PI) * erf(sqrt(x)), iGammaP(.5, x) * gamma(.5),
            EPSILON);
        // γ(a, x) → Γ(a) for x → ∞
        assertEquals(1.0, iGammaP(x, POSITIVE_INFINITY));
        // γ(a, 0) = 0
        assertEquals(0.0, iGammaP(x, 0));
      }
    }
  }

  /**
   * Tests whether the correct exceptions are thrown when passing invalid
   * arguments to the {@link IncompleteGammaFunction#iGammaQ(double, double)}
   * method.
   */
  public void testIGammaQExceptions() {
    try {
      // a negative
      iGammaQ(-1, 1);
      fail();
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // x negative
      iGammaQ(1, -1);
      fail();
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }

    try {
      // a and x negative
      iGammaQ(-1, -1);
      fail();
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    } catch (Throwable t) {
      fail();
    }
  }

  /**
   * Tests special function values for the
   * {@link IncompleteGammaFunction#iGammaQ(double, double)} method.
   */
  public void testIGammaQSpecialValues() {
    // some selected values
    assertEquals(0.977589297761649, iGammaQ(1.5, 0.1), EPSILON);
    assertEquals(0.0883764323567855, iGammaQ(3, 5.5), EPSILON);
    assertEquals(0.012719807581195, iGammaQ(3, 8.1), EPSILON);
    assertEquals(0.945752973843811, iGammaQ(1.2, 0.1), EPSILON);
    assertEquals(0.90979598956895, iGammaQ(2, 0.5), EPSILON);
    assertEquals(0.0, iGammaQ(2, 90), EPSILON);
    assertEquals(3.0613957483494E-06, iGammaQ(2, 15.5), EPSILON);
    assertEquals(0.0, iGammaQ(9.95e304, 1.0499e308));
  }

  /**
   * Tests general properties for the
   * {@link IncompleteGammaFunction#iGammaQ(double, double)} method.
   */
  public void testIGammaQProperties() {
    for (double x : dataPoints) {
      if (x > 0) {
        // Γ(a, 0) = Γ(a)
        assertEquals(1.0, iGammaQ(x, 0));
        // Γ(1, x) = e^-x
        assertEquals(exp(-x), iGammaQ(1, x), EPSILON);
        // Γ(1/2, x) = √π erfc(√x)
        assertEquals(sqrt(PI) * erfc(sqrt(x)), iGammaQ(.5, x) * gamma(.5),
            EPSILON);
      }
    }
  }

  /**
   * Tests special properties concerning both the
   * {@link IncompleteGammaFunction#iGammaP(double, double)} and the
   * {@link IncompleteGammaFunction#iGammaQ(double, double)} method.
   */
  public void testIGammaProperties() {
    for (double x : dataPoints) {
      if (x > 0) {
        for (double a = 0.1; x < 8; x += .01) {
          // γ(a, x) + Γ(a, x) = Γ(a)
          assertEquals(1.0, iGammaP(a, x) + iGammaQ(a, x), EPSILON);
        }
      }
    }
  }

}
