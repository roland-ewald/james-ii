/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.misc.Arrays;

/**
 * This class executes an interpolation dividing the problem into intervals and
 * using cubic splines to interpolate each interval.
 * 
 * @author Stefan Leye
 */

public class CubicSplineInterpolator implements IInterpolator {

  /** The given x values. */
  private List<Double> xValues;

  /** The given y values. */
  private List<Double> yValues;

  /** The curves to calculate the splines. */
  private List<Double> curves = new ArrayList<>();

  /** The parameters for the curve polynomials. */
  private List<Double[]> polynomials = new ArrayList<>();

  /** The lengths of the curves. */
  private List<Double> lengths = new ArrayList<>();

  @Override
  public Double getOrdinateAtPosition(double abscisse) {
    if (polynomials.isEmpty()) {
      initializePolynomials();
    }
    int i = Arrays.binarySearch(xValues, abscisse, 0, xValues.size() - 1);
    Double[] poly = polynomials.get(i);
    double diff = abscisse - xValues.get(i);
    double result =
        poly[0] * diff * diff * diff + poly[1] * diff * diff + poly[2] * diff
            + poly[3];
    return result;
  }

  /**
   * Calculates the coefficients of the spline polynomials.
   */
  private void initializePolynomials() {
    if (curves.isEmpty()) {
      initializeCurves();
    }
    // calculate the coefficients of each polynomial
    for (int i = 0; i < curves.size() - 1; i++) {
      Double[] coeffs = new Double[4];
      coeffs[0] = (curves.get(i + 1) - curves.get(i)) / (6 * lengths.get(i));
      coeffs[1] = curves.get(i) / 2;
      coeffs[2] =
          ((yValues.get(i + 1) - yValues.get(i)) / lengths.get(i))
              - ((lengths.get(i) / 6) * ((2 * curves.get(i)) + curves
                  .get(i + 1)));
      coeffs[3] = yValues.get(i);
      polynomials.add(i, coeffs);
    }
  }

  /**
   * Calculates the curves, which are necessary to calculate the spline
   * polynomials.
   */
  private void initializeCurves() {
    List<Double> e = new ArrayList<>();
    int n = xValues.size() - 1;
    // calculate length of intervals
    for (int i = 0; i <= n - 1; i++) {
      lengths.add(i, xValues.get(i + 1) - xValues.get(i));
      e.add(i, (6 / lengths.get(i)) * (yValues.get(i + 1) - yValues.get(i)));
    }
    List<Double> u = new ArrayList<>();
    List<Double> r = new ArrayList<>();
    // tri-diagonal equation system and forward insertion
    u.add(0, 2 * lengths.get(1) + lengths.get(0));
    r.add(0, e.get(1) - e.get(0));
    for (int i = 1; i <= n - 1; i++) {
      double uValue =
          (2 * (lengths.get(i) + lengths.get(i - 1)))
              - ((lengths.get(i - 1) * lengths.get(i - 1)) / u.get(i - 1));
      u.add(i, uValue);
      double rValue =
          (e.get(i) - e.get(i - 1))
              - ((r.get(i - 1) * lengths.get(i - 1)) / u.get(i - 1));
      r.add(i, rValue);
    }
    // calculate the curves
    curves.add(0.0);
    for (int i = n - 1; i > 0; i--) {
      curves.add(0, (r.get(i) - (lengths.get(i) * curves.get(0))) / u.get(i));
    }
    curves.add(0, 0.0);
  }

  @Override
  public List<Double> getXValues() {
    return xValues;
  }

  @Override
  public void setXValues(List<Double> xValues) {
    this.xValues = xValues;
  }

  @Override
  public List<Double> getYValues() {
    return yValues;
  }

  @Override
  public void setYValues(List<Double> yValues) {
    this.yValues = yValues;
  }

}
