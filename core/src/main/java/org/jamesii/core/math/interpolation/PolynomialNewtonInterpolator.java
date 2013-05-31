/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class executes an interpolation according to the Newton algorithm.
 * 
 * @author Stefan Leye
 */

public class PolynomialNewtonInterpolator implements IInterpolator {

  /** The given x values. */
  private List<Double> xValues;

  /** The given y values. */
  private List<Double> yValues;

  /** The coefficients of the resulting polynomial. */
  private List<Double> coefficients = new ArrayList<>();

  /**
   * Calculates the result of the interpolation polynomial for a specific value.
   * 
   * @param abscisse
   *          the abscisse
   * 
   * @return the ordinate at position
   */
  @Override
  public Double getOrdinateAtPosition(double abscisse) {
    double result = 0.0;
    // initialize the coefficients
    if (coefficients.isEmpty()) {
      for (int i = 0; i < xValues.size(); i++) {
        coefficients.add(helpPolynomial(i, i));
      }
    }
    // calculate the interpolated value
    for (int i = xValues.size() - 1; i >= 0; i--) {
      result = result * (abscisse - xValues.get(i)) + coefficients.get(i);
    }
    return result;
  }

  /**
   * Returns the result of the kth help polynomial with the ith x value.
   * 
   * @param k
   *          the k
   * @param i
   *          the i
   * 
   * @return the double
   */
  private double helpPolynomial(int k, int i) {
    if (k == 0) {
      return yValues.get(i);
    }
    if (coefficients.size() < k) {
      coefficients.add(k - 1, helpPolynomial(k - 1, k - 1));
    }
    return (helpPolynomial(k - 1, i) - coefficients.get(k - 1))
        / (xValues.get(i) - xValues.get(k - 1));
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
