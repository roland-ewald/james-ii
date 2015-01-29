/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static org.jamesii.core.math.Factorial.fac;
import static org.jamesii.core.math.statistics.univariate.GammaFunction.gamma;
import static org.jamesii.core.math.statistics.univariate.GammaFunction.gammaHalfInt;
import static org.jamesii.core.math.statistics.univariate.GammaFunction.logGamma;

import org.jamesii.core.math.statistics.StatisticsTest;

/** Tests the {@link GammaFunction} class. */
public class GammaFunctionTest extends StatisticsTest {

  /** Accuracy for the tests. */
  protected double EPSILON = 1e-13;

  /** Data points for testing. */
  double[] dataPoints = { 1.1, -0.1, 1.5, 3.2, -PI, PI, 0.9, 0.75, 0.5, -0.5,
      -0.75, 150.5, -150.5, 171.5, -169.1 };

  /**
   * Tests special function values for the {@link GammaFunction#gamma(double)}
   * method.
   */
  public void testGammaSpecialValues() {
    // gamma(n) = (n-1)!
    assertEquals(1.0, gamma(1.0), EPSILON);
    assertEquals(1.0, gamma(2.0), EPSILON);
    assertEquals(2.0, gamma(3.0), EPSILON);
    assertEquals(6.0, gamma(4.0), EPSILON);
    assertEquals(24.0, gamma(5.0), EPSILON);
    assertEquals(120.0, gamma(6.0), EPSILON);
    assertEquals(720.0, gamma(7.0), EPSILON);
    assertEquals(5040.0, gamma(8.0), EPSILON);
    assertEquals(40320.0, gamma(9.0), EPSILON);
    assertEquals(362880.0, gamma(10.0), EPSILON);

    // some more special values (see
    // http://functions.wolfram.com/GammaBetaErf/Gamma/03/02/)
    assertEquals(-8.0 / 15.0 * sqrt(PI), gamma(-2.5), EPSILON);
    assertEquals(4.0 / 3.0 * sqrt(PI), gamma(-1.5), EPSILON);
    assertEquals(-2.0 * sqrt(PI), gamma(-0.5), EPSILON);
    assertEquals(sqrt(PI), gamma(0.5), EPSILON);
    assertEquals(sqrt(PI) / 2.0, gamma(1.5), EPSILON);
    assertEquals(3.0 / 4.0 * sqrt(PI), gamma(2.5), EPSILON);

    // even more (see
    // http://en.wikipedia.org/wiki/Particular_values_of_the_Gamma_function)
    assertEquals(2.6789385347077476337, gamma(1 / 3.0), EPSILON);
    assertEquals(3.6256099082219083119, gamma(1 / 4.0), EPSILON);
    assertEquals(4.5908437119988030532, gamma(1 / 5.0), EPSILON);
    assertEquals(5.5663160017802352043, gamma(1 / 6.0), EPSILON);
    assertEquals(6.5480629402478244377, gamma(1 / 7.0), EPSILON);
    // local minimum
    assertEquals(0.885603194410888, gamma(1.461632144968362341262), EPSILON);

    // some more special values to find some special code paths
    assertEquals(9.999999999999422784e12, gamma(1e-13), 1e-2);
    assertEquals(-1.0000000000000577216e13, gamma(-1e-13), 1e-2);
    assertEquals(4.2690680090047052749e304, gamma(170), 1e289);
    assertEquals(4.9745042224772874404e217, gamma(130));
    assertEquals(2.9331459726198887132e-41, gamma(-35.75));
    assertEquals(Double.NaN, gamma(-1));
    assertEquals(Double.NaN, gamma(0));
    assertEquals(Double.NaN, gamma(Double.NaN));

    // infinities
    assertEquals(Double.POSITIVE_INFINITY, gamma(Double.POSITIVE_INFINITY),
        EPSILON);
    assertEquals(Double.NaN, gamma(Double.NEGATIVE_INFINITY), EPSILON);

    // some intervals, check for positivity and negativity
    double[][] negativeIntervals =
        { { -9, -8 }, { -7, -6 }, { -5, -4 }, { -3, -2 }, { -1, 0 } };
    for (double[] interval : negativeIntervals) {
      for (double x = interval[0] + 0.001; x < interval[1]; x += 0.005) {
        assertTrue("result was not negative", gamma(x) < 0);
      }
    }
    double[][] positiveIntervals =
        { { -8, -7 }, { -6, -5 }, { -4, -3 }, { -2, -1 }, { 0, 15 } };
    for (double[] interval : positiveIntervals) {
      for (double x = interval[0] + 0.001; x < interval[1]; x += 0.005) {
        assertTrue(gamma(x) + " was not positive", gamma(x) > 0);
      }
    }
  }

  /**
   * Tests special function values for the
   * {@link GammaFunction#gammaHalfInt(int)} method.
   */
  public void testGammaIntAndHalfIntValues() {
    // gamma(n) = (n-1)!
    assertEquals(1.0, gammaHalfInt(2), EPSILON);
    assertEquals(1.0, gammaHalfInt(4), EPSILON);
    assertEquals(2.0, gammaHalfInt(6), EPSILON);
    assertEquals(6.0, gammaHalfInt(8), EPSILON);
    assertEquals(24.0, gammaHalfInt(10), EPSILON);
    assertEquals(120.0, gammaHalfInt(12), EPSILON);
    assertEquals(720.0, gammaHalfInt(14), EPSILON);
    assertEquals(5040.0, gammaHalfInt(16), EPSILON);
    assertEquals(40320.0, gammaHalfInt(18), EPSILON);
    assertEquals(362880.0, gammaHalfInt(20), EPSILON);

    // assertEquals(sqrt(PI), gammaHalfInt(1), EPSILON);
    // ^results in semi-factorial call with arg -1, then exception
    assertEquals(sqrt(PI) / 2.0, gammaHalfInt(3), EPSILON);
    assertEquals(3.0 / 4.0 * sqrt(PI), gammaHalfInt(5), EPSILON);
  }

  /**
   * Tests general properties for the {@link GammaFunction#gamma(double)}
   * method.
   */
  public void testGammaProperties() {
    for (double x : dataPoints) {
      // Euler's reflection formula - doesn't work with many possible values
      // because they only work reliably as complex numbers. Thus the usefulness
      // of this test may be debatable as I had to actively search for
      // data points that do not generate NaN (usually non-integer values).
      assertEquals(PI / sin(PI * x), gamma(1 - x) * gamma(x), EPSILON);

      // duplication formula
      assertEquals(Math.pow(2, 1 - 2 * x) * Math.sqrt(Math.PI) * gamma(2 * x),
          gamma(x) * gamma(x + 0.5), EPSILON);
    }
  }

  /** Tests the {@link GammaFunction#logGamma(double)} method. */
  public void testLogGamma() {
    for (int n = 0; n < 10; n++) {
      // logΓ(-n) = ∞; n ∊ ℕ
      assertEquals(POSITIVE_INFINITY, logGamma(-n));
      // logΓ(n) = log((n-1)!); n ∊ ℕ⁺
      if (n > 0) {
        assertEquals(log(fac(n - 1)), logGamma(n), EPSILON);
      }
    }

    // some selected values
    assertEquals(0.0, logGamma(2), EPSILON);
    assertEquals(1.9187771947649628, logGamma(4.1), EPSILON);
    assertEquals(1.2009736023470745, logGamma(3.5), EPSILON);
    assertEquals(0.055265352801649806, logGamma(2.12), EPSILON);
    assertEquals(42.940693520199204, logGamma(21.2), EPSILON);
    assertEquals(24728.392630601, logGamma(3459.5), EPSILON);
    assertEquals(0.8261387047770284, logGamma(3.14), EPSILON);
    assertEquals(4.599479878042022, logGamma(0.01), EPSILON);

    // some more values to trigger some edge cases in the calculation
    assertEquals(NaN, logGamma(NaN));
    assertEquals(POSITIVE_INFINITY, logGamma(POSITIVE_INFINITY));
    assertEquals(-89.21020037996898, logGamma(-34.5), EPSILON);
    assertEquals(-93.32991325495028, logGamma(-35.75), EPSILON);
    assertEquals(POSITIVE_INFINITY, logGamma(-35), EPSILON);
    assertEquals(3.8141497899607954e9, logGamma(2.1e8), EPSILON);
    assertEquals(POSITIVE_INFINITY, logGamma(2.6e305), EPSILON);
  }

}
