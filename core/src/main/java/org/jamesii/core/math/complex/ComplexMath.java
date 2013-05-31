/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.complex;

/**
 * Provides numerous advanced operations on complex numbers that lie outside of
 * the basic operators. This class is intended to be similar to the {@link Math}
 * class.
 * 
 * @author Johannes Rössel
 */
public final class ComplexMath {

  /**
   * Private default constructor to prevent anyone of instantiating this class.
   */
  private ComplexMath() {
  }

  /**
   * Absolute value of a complex number.
   * 
   * @param c
   *          the complex number to operate on.
   * 
   * @return the double
   */
  public static double abs(Complex c) {
    return c.getModulus();
  }

  /**
   * Calculates the power of a complex number to a real number.
   * 
   * @param a
   *          Base.
   * @param b
   *          Exponent.
   * @return <i>a</i><sup><i>b</i></sup>.
   */
  public static Complex pow(Complex a, double b) {
    return Complex.fromPolar(Math.pow(a.getModulus(), b), a.getArgument() * b);
  }

  /**
   * Calculates the power of a complex number to a complex number.
   * 
   * @param a
   *          Base.
   * @param b
   *          Exponent.
   * @return <i>a</i><sup><i>b</i></sup>.
   */
  public static Complex pow(Complex a, Complex b) {
    return Complex.fromPolar(
        Math.pow(
            a.getReal() * a.getReal() + a.getImaginary() * a.getImaginary(),
            b.getReal() / 2)
            * Math.exp(-b.getImaginary() * a.getArgument()),
        b.getReal()
            * a.getArgument()
            + .5
            * b.getImaginary()
            * Math.log(a.getReal() * a.getReal() + a.getImaginary()
                * a.getImaginary()));
  }

  /**
   * Calculates the <i>n</i><sup>th</sup> roots of the complex number <i>c</i>,
   * that is, complex solutions <i>z</i> of the equation
   * <i>z</i><sup><i>n</i></sup> = <i>c</i>.
   * 
   * @param n
   *          The exponent.
   * @param c
   *          The radicand. Can be negative.
   * @return An array of complex numbers, each of which is a
   *         <i>n</i><sup>th</sup> root of <i>c</i>. the array has exactly
   *         |<i>n</i>| values.
   */
  public static Complex[] root(int n, Complex c) {
    if (c.equals(new Complex(0, 0)) && n < 0) {
      return new Complex[0];
    }
    Complex[] ret = new Complex[Math.abs(n)];
    double r = Math.pow(c.getModulus(), 1d / n);
    double phiOverN = c.getArgument() / n;
    double twoPiOverN = 2 * Math.PI / n;
    /*
     * I know, premature optimization is evil, but anyway: I tried rewriting the
     * code below with just an addition and an assignment per iteration (by just
     * adding twoPiOverN in each iteration). Turns out that this affected
     * performance adversely. The loss in accuracy however was negligible. By
     * calculating the 200,000th root of a complex number I just got a deviation
     * of 2e-18. The code below here, however is accurate and more than two
     * times faster than the naive loop. And about 30 percent faster than the
     * allegedly more optimized code. -- Johannes Rössel
     */
    for (int k = 0; k < Math.abs(n); k++) {
      ret[k] = Complex.fromPolar(r, phiOverN + k * twoPiOverN);
    }
    return ret;
  }

  /**
   * Square root of a complex number.
   * 
   * @param c
   *          A complex number.
   * 
   * @return All roots <i>z</i> of the equation <i>c</i> = <i>z</i><sup>2</sup>
   */
  public static Complex[] sqrt(Complex c) {
    return root(2, c);
  }

  /**
   * Cubic root of a complex number.
   * 
   * @param c
   *          A complex number.
   * 
   * @return All roots <i>z</i> of the equation <i>c</i> = <i>z</i><sup>3</sup>
   */
  public static Complex[] cbrt(Complex c) {
    return root(3, c);
  }

}
