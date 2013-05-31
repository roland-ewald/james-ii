/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import org.jamesii.core.math.complex.Complex;

/**
 * This class performs a Fast Fourier Transform on an array of complex input
 * values. This class is adapted from Robert Sedgewick's Algorithms in Java.
 * According to his own comments about it it is very basic and not very fast
 * (although that lies partly with the implementation of {@link Complex}. If we
 * experience performance issues, this class might be replaced, however, this
 * should be of minimal concern as of now.
 * <p>
 * The FFT can only be fast (and thus is only defined) for array lengths of
 * powers of two.
 * <p>
 * TODO: Finish ComplexArray1D and adapt this class to use it.
 * 
 * @author Johannes Rössel
 */
public final class FFT {

  /** Private empty constructor to prevent instantiation. */
  private FFT() {
  }

  /**
   * Transform: compute the FFT of x[], assuming its length is a power of 2
   * 
   * @param x
   *          the x
   * 
   * @return the complex[]
   */
  public static Complex[] transform(Complex[] x) {
    int n = x.length;

    // base case
    if (n == 1) {
      return new Complex[] { x[0] };
    }

    // radix 2 Cooley-Tukey FFT
    // TODO: Is this correct? % 2 does not check for powers of two but rather
    // for multiples of two. Here may be an error.
    if (n % 2 != 0) {
      throw new ArithmeticException("N is not a power of 2");
    }

    // fft of even terms
    Complex[] even = new Complex[n / 2];
    for (int k = 0; k < n / 2; k++) {
      even[k] = x[2 * k];
    }
    Complex[] q = transform(even);

    // fft of odd terms
    Complex[] odd = even; // reuse the array
    for (int k = 0; k < n / 2; k++) {
      odd[k] = x[2 * k + 1];
    }
    Complex[] r = transform(odd);

    // combine
    Complex[] y = new Complex[n];
    for (int k = 0; k < n / 2; k++) {
      double kth = -2 * k * Math.PI / n;
      Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
      y[k] = q[k].add(wk.multiply(r[k]));
      y[k + n / 2] = q[k].subtract(wk.multiply(r[k]));
    }
    return y;
  }

  /**
   * Inverse transform. Compute the inverse FFT of x[], assuming its length is a
   * power of 2
   * 
   * @param x
   *          the x
   * 
   * @return the complex[]
   */
  public static Complex[] inverseTransform(Complex[] x) {
    int r = x.length;
    Complex[] y = new Complex[r];

    // take conjugate
    for (int i = 0; i < r; i++) {
      y[i] = x[i].conjugate();
    }

    // compute forward FFT
    y = transform(y);

    // take conjugate again
    for (int i = 0; i < r; i++) {
      y[i] = y[i].conjugate();
    }

    // divide by N
    for (int i = 0; i < r; i++) {
      y[i] = y[i].multiply(1.0 / r);
    }

    return y;

  }

}
