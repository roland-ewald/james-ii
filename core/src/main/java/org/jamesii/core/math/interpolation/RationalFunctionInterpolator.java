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
 * This class executes a Barycentric Rational Interpolation without Poles.
 * 
 * First set xValues and yValues. After that you can use getOrdinateAtPosition
 * to calculate the ordinate at a specific abscissa.
 * 
 * @author Rene Schulz
 */

public class RationalFunctionInterpolator implements IInterpolator {

  /** The given x values. */
  private List<Double> xValues;

  /** The given y values. */
  private List<Double> yValues;

  /**
   * (Number of the given (x,y)-pairs)-1 = maximal Index-Range for
   * xValues,yValues and Weights
   */
  private int n;

  /** Calculated Weights of the (x,y)-pairs */
  private List<Double> weights = new ArrayList<>();

  @Override
  public Double getOrdinateAtPosition(double abscisse) {
    if (weights.isEmpty()) {
      calcWeights();
    }

    /** Interpolation needs at least 2 (x,y)-pairs to work */
    if (n < 1) {
      return null;
    }

    double numerator = 0.0;
    double denominator = 0.0;

    /**
     * If abscisse isn't in the given xValues, calculate interpolation value,
     * else return the given y-Value
     */
    for (int i = 0; i <= n; i++) {
      if (Double.compare(abscisse, xValues.get(i)) != 0) {
        numerator =
            numerator
                + (weights.get(i) / (abscisse - xValues.get(i)) * yValues
                    .get(i));
        denominator =
            denominator + (weights.get(i) / (abscisse - xValues.get(i)));
      } else {
        return yValues.get(i);
      }
    }

    return (numerator / denominator);

  }

  @Override
  public List<Double> getXValues() {
    return xValues;
  }

  @Override
  public List<Double> getYValues() {
    return yValues;
  }

  @Override
  public void setXValues(List<Double> values) {
    xValues = values;

  }

  @Override
  public void setYValues(List<Double> values) {
    yValues = values;

  }

  /**
   * Calculates the weights used in the rational function, according to the
   * given (x,y) pairs.
   * 
   */
  private void calcWeights() {
    /** determine n (Number of given (x,y)-pairs */
    if (xValues.size() < yValues.size()) {
      n = xValues.size();
    } else {
      n = yValues.size();
    }
    /**
     * n is number of given pairs -1 = maximal Index-Range for weights, xValues,
     * yValues
     */
    n--;

    weights.clear();

    /** calculate weights 0 */
    if (n >= 0) {
      weights.add((-1) / (xValues.get(1) - xValues.get(0)));
    }

    /** calculate weights from 1 up to (n-1) */
    for (int i = 1; i < n; i++) {
      weights
          .add((Math.pow(-1, i - 1))
              * (1 / (xValues.get(i + 1) - xValues.get(i)) + (1 / (xValues
                  .get(i) - xValues.get(i - 1)))));
    }

    /** calculate weights n */
    if (n >= 1) {
      weights.add(Math.pow(-1, n - 1) / (xValues.get(n) - xValues.get(n - 1)));
    }

  }

}
