/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

/**
 * Class for calculations of the error function (erf) and related functions
 * (such as the complementary error function (erfc).
 * <p>
 * These methods were adapted from the <a
 * href="http://www.netlib.org/cephes/">Cephes Mathematical Library</a> by <a
 * href="http://www.moshier.net">Stephen L. Moshier</a>.
 * 
 * @author Johannes Rössel
 * @author Roland Ewald
 * @author Arne Bittig
 */
public final class ErrorFunction {

  /** Private constructor to prevent instantiation. */
  private ErrorFunction() {
  }

  /**
   * Calculates the error function <b>(erf)</b> which is defined as
   * <p>
   * <b>erf</b>(<i>z</i>) = 2/√<em style="text-decoration: overline">π</em>
   * ∫<sub>0</sub><sup><i>z</i></sup> <i>e</i><sup>−<i>t</i><sup>2</sup></sup>
   * d<i>t</i>
   * 
   * @param z
   *          The argument to <b>erf</b>.
   * @return <b>erf</b>(<i>z</i>).
   */
  public static double erf(double z) {
    if (Math.abs(z) > 1.0) {
      return 1.0 - erfc(z);
    }
    double y, x;
    x = z * z;
    y =
        z
            * ((((9.60497373987051638749E0 * x + 9.00260197203842689217E1) * x + 2.23200534594684319226E3) // NOSONAR
                * x + 7.00332514112805075473E3)// NOSONAR
                * x + 5.55923013010394962768E4)// NOSONAR
            / (((((x + 3.35617141647503099647E1) * x + 5.21357949780152679795E2)// NOSONAR
                * x + 4.59432382970980127987E3)// NOSONAR
                * x + 2.26290000613890934246E4)// NOSONAR
                * x + 4.92673942608635921086E4);// NOSONAR

    return y;
  }

  /**
   * Calculates the complementary error function <b>(erfc)</b> which is defined
   * as
   * <p>
   * <b>erfc</b>(<i>z</i>) = 1 − <b>erf</b>(<i>z</i>).
   * 
   * @param z
   *          The argument to <b>erfc</b>.
   * @return <b>erfc</b>(<i>z</i>).
   */
  public static double erfc(double z) {
    double p, q, x, ans, y;

    if (z < 0.0) {
      x = -z;
    } else {
      x = z;
    }

    if (x < 1.0) {
      return 1.0 - erf(z);
    }

    y = -z * z;

    if (y < -7.09782712893383996843E2) {// NOSONAR
      if (z < 0) {
        return 2.0;
      }

      return 0.0;
    }

    y = Math.exp(y);

    if (x < 8.0) {
      p = ((((((((2.46196981473530512524E-10 * x + 5.64189564831068821977E-1)// NOSONAR
          * x + 7.46321056442269912687E0)// NOSONAR
          * x + 4.86371970985681366614E1)// NOSONAR
          * x + 1.96520832956077098242E2)// NOSONAR
          * x + 5.26445194995477358631E2)// NOSONAR
          * x + 9.34528527171957607540E2)// NOSONAR
          * x + 1.02755188689515710272E3)// NOSONAR
          * x + 5.57535335369399327526E2);// NOSONAR
      q = ((((((((x + 1.32281951154744992508E1) * x + 8.67072140885989742329E1)// NOSONAR
          * x + 3.54937778887819891062E2)// NOSONAR
          * x + 9.75708501743205489753E2)// NOSONAR
          * x + 1.82390916687909736289E3)// NOSONAR
          * x + 2.24633760818710981792E3)// NOSONAR
          * x + 1.65666309194161350182E3)// NOSONAR
          * x + 5.57535340817727675546E2);// NOSONAR
    } else {
      p =
          (((((5.64189583547755073984E-1 * x + 1.27536670759978104416E0) * x + 5.01905042251180477414E0)// NOSONAR
              * x + 6.16021097993053585195E0)// NOSONAR
              * x + 7.40974269950448939160E0)// NOSONAR
              * x + 2.97886665372100240670E0);// NOSONAR
      q = ((((((x + 2.26052863220117276590E0) * x + 9.39603524938001434673E0)// NOSONAR
          * x + 1.20489539808096656605E1)// NOSONAR
          * x + 1.70814450747565897222E1)// NOSONAR
          * x + 9.60896809063285878198E0)// NOSONAR
          * x + 3.36907645100081516050E0);// NOSONAR
    }

    ans = (y * p) / q;

    if (z < 0) {
      ans = 2.0 - ans;
    }

    if (ans == 0.0) {
      if (z < 0) {
        return 2.0;
      }

      return 0.0;
    }

    return ans;
  }

  /**
   * Computes inverse of the complementary error function erfc. This is done by
   * a <a href="http://mathworld.wolfram.com/InverseErf.html">Maclaurin
   * series</a>, more precisely, the simplified form, for 6 terms. Gets
   * imprecise for values above 0.7.
   * 
   * @param x
   *          the function parameter
   * @return erf^-1(x)
   * @see #inverf(double, int)
   * @see #inverf(double, double)
   */
  public static double inverf(double x) {
    return inverf(x, 5);
  }

  /**
   * Computes inverse of the complementary error function erfc. This is done by
   * a <a href="http://mathworld.wolfram.com/InverseErf.html">Maclaurin
   * series</a>. However, inverf(erf(x)) is only approximately given (with
   * precision better than 0.001) for x in [0,3].
   * 
   * @param z
   *          the parameter
   * 
   * @return erf^-1(z)
   */
  @Deprecated
  // originally present version based on the non-simplified form and with
  // explicit powers-of-z calculations for each term (gives a much coarser
  // approximation), but with coefficients extracted as constants
  public static double inverfConst(double z) {
    double result =
        z + PI_1_12 * Math.pow(z, 3) + PI_7_480 * Math.pow(z, 5) + PI_127_40320
            * Math.pow(z, 7) + PI_4369_5806080 * Math.pow(z, 9)
            + PI_34807_182476800 * Math.pow(z, 11);
    return HALF_SQRT_PI * result;
  }

  private static final double PI_1_12 = Math.PI / 12;

  private static final double PI_7_480 = 7 * Math.PI / 480;

  private static final double PI_127_40320 = 127 * Math.PI / 40320;

  private static final double PI_4369_5806080 = 4369 * Math.PI / 5806080;

  private static final double PI_34807_182476800 = 34807 * Math.PI / 182476800;

  private static final double HALF_SQRT_PI = 0.5 * Math.sqrt(Math.PI);

  /**
   * Coefficients for inverf(2*x/sqrt(pi)); see http://oeis.org/A092676 and
   * http://oeis.org/A092677
   */
  private static final double[] SIMPLE_COEFFS = new double[] { 1. / 3.,
      7. / 30., 127. / 630., 4369. / 22680., 34807. / 178200.,
      20036983. / 97297200., 2280356863. / 10216206000.,
      49020204823. / 198486288000., 65967241200001. / 237588086736000.,
      15773461423793767. / 49893498214560000.,
      655889589032992201. / 1803293578326240000.,
      94020690191035873697. / 222759794969712000000.,
      655782249799531714375489. / 1329207696584271504000000. };

  /**
   * inverf(x) approximated by given number of terms of the Maclaurin simplified
   * series
   * 
   * @param x
   *          Function parameter
   * @param precision
   *          number of terms of the Maclaurin series -1
   * @return erf^-1(x)
   */
  public static double inverf(double x, int precision) {
    double res = x * HALF_SQRT_PI;
    double xsqr = res * res;
    double xpow = res * xsqr;
    for (int i = 0; i < precision; i++) {
      res += SIMPLE_COEFFS[i] * xpow;
      xpow *= xsqr;
    }
    return res;
  }

  /**
   * inverf(x) approximated by Maclaurin simplified series until last term is
   * smaller than given delta
   * 
   * @param x
   *          Function parameter
   * @param delta
   *          approximation precision
   * @return erf^-1(x)
   */
  public static double inverf(double x, double delta) {
    double res = x * HALF_SQRT_PI;
    double xsqr = res * res;
    double xpow = res * xsqr;
    int i = 0;
    double add;
    do {
      add = SIMPLE_COEFFS[i] * xpow;
      res += add;
      xpow *= xsqr;
      i++;
    } while (add > delta && i < SIMPLE_COEFFS.length);
    return res;
  }

  /**
   * Computes inverse complementary function by falling back to
   * {@link ErrorFunction#inverf(double)}. Gets imprecise for values below 0.3 =
   * 1-0.7.
   * 
   * @param z
   *          the parameter
   * @return erfc^-1(z)
   */
  public static double inverfc(double z) {
    return inverf(1 - z);
  }

  /**
   * Computes inverse complementary function with given precision (if possible).
   * 
   * @param x
   *          Function parameter
   * @param delta
   *          approximation precision
   * @return erfc^-1(x)
   */
  public static double inverfc(double x, double delta) {
    return inverf(1 - x, delta);
  }
}
