/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.misc.Arrays;

/**
 * This class performs a weighted spline interpolation on given pairs of values.
 * 
 * @author Thomas Flisgen
 */

public class WeightedSplineInterpolator implements IInterpolator {

  /** The x values. */
  private List<Double> xValues;

  /** The y values. */
  private List<Double> yValues;

  /** Length of intervals. */
  private List<Double> h = new ArrayList<>();

  /** The tension parameter. */
  private List<Double> p = new ArrayList<>();

  /** Interpolation parameter c. */
  private List<Double> c = new ArrayList<>();

  /** Interpolation parameter b. */
  private List<Double> b = new ArrayList<>();

  /** Interpolation parameter D. */
  private List<Double> d = new ArrayList<>();

  /**
   * Initilize the vectors.
   */
  private void initVectors() {
    for (int i = 0; i <= yValues.size() - 2; i++) {
      d.add(i, 0.0);

      c.add(i, 0.0);
      b.add(i, 0.0);

      h.add(i, 0.0);
      p.add(i, 1.0);
    }
    d.add(yValues.size() - 1, 0.0);

  }

  /**
   * Compute parameters.
   */
  private void computeParameters() {

    // Number of pairs to interpolate
    int n = xValues.size();

    // Precompute helping variables
    for (int i = 0; i <= n - 2; i++) {
      h.set(i, xValues.get(i + 1) - xValues.get(i));
      c.set(i, p.get(i) / h.get(i));
      b.set(i, 3 * c.get(i) / h.get(i));
    }

    // Variables used to solve the tri-diagonal system
    List<Double> y = new ArrayList<>();
    List<Double> u = new ArrayList<>();
    List<Double> l = new ArrayList<>();

    // LU - Decomposition for a tri-diagonal matrix, A = L*U

    // L*U*x = b <=> L*y = b
    // U*x=y

    // Calculation of L and U, where the diagonals of the matrix are stored in
    // vector l and u
    u.add(0, 2 * c.get(0));

    for (int i = 0; i <= n - 3; i++) {
      l.add(i, c.get(i) / u.get(i));
      u.add(i + 1, 2 * c.get(i) + 2 * c.get(i + 1) - l.get(i) * c.get(i));
    }

    l.add(n - 2, c.get(n - 2) / u.get(n - 2));
    u.add(n - 1, 2 * c.get(n - 2) - l.get(n - 2) * c.get(n - 2));

    // Forward elimination
    y.add(0, b.get(0) * (yValues.get(1) - yValues.get(0)));

    for (int i = 1; i <= n - 2; i++) {
      y.add(i, b.get(i) * (yValues.get(i + 1) - yValues.get(i)) + b.get(i - 1)
          * (yValues.get(i) - yValues.get(i - 1)) - l.get(i - 1) * y.get(i - 1));
    }
    y.add(n - 1,
        b.get(n - 2) * (yValues.get(n - 1) - yValues.get(n - 2)) - l.get(n - 2)
            * y.get(n - 2));

    // Backward elimination
    d.set(n - 1, y.get(n - 1) / u.get(n - 1));
    for (int i = n - 2; i >= 0; i -= 1) {
      d.set(i, (y.get(i) - c.get(i) * d.get(i + 1)) / u.get(i));
    }

    y.clear();
    u.clear();
    l.clear();
  }

  @Override
  public Double getOrdinateAtPosition(double abscisse) {
    double val;

    // Find the right interval
    int i = Arrays.binarySearch(xValues, abscisse, 0, xValues.size() - 1);

    // Compute helping variables
    // TODO h has not been computed for i outside of xValues
    double theta = (abscisse - xValues.get(i)) / h.get(i);
    double v = yValues.get(i) + h.get(i) * d.get(i) / 3.0;
    double w = yValues.get(i + 1) - h.get(i) * d.get(i + 1) / 3.0;

    // Compute return value
    val =
        yValues.get(i) * Math.pow(1 - theta, 3) + 3 * theta
            * Math.pow(1 - theta, 2) * v + 3 * Math.pow(theta, 2) * (1 - theta)
            * w + yValues.get(i + 1) * Math.pow(theta, 3);

    return val;
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
  public void setXValues(List<Double> xValues) {
    this.xValues = xValues;
  }

  /**
   * Compute tension factor.
   */
  private void computeTensionFactor() {
    double f1, f2;

    // Inflexion behaviour computed by discrete points
    List<Double> inflexion_data = new ArrayList<>();

    // Inflexion behaviour computed by second derivative of spline functions
    List<Double> inflexion_spline = new ArrayList<>();

    boolean ready;

    // Fit the tension parameters as long as unwanted inflexion points occur
    do {

      ready = true;

      for (int k = 0; k <= yValues.size() - 3; k++) {

        // Compute inflexion by means of discrete data points
        f1 = (yValues.get(k + 2) - yValues.get(k + 1)) / h.get(k + 1);
        f2 = (yValues.get(k + 1) - yValues.get(k)) / h.get(k);
        inflexion_data.add((f1 - f2) / (0.5 * (h.get(k) + h.get(k + 1))));

        double w = yValues.get(k + 1) - h.get(k) * d.get(k + 1) / 3;// y(i+1)-h(i)*D(i+1)/3;
        double v = yValues.get(k) + h.get(k) * d.get(k) / 3;// y(i) +
                                                            // h(i)*D(i)/3;

        // Compute inflexion by means of spline parameters
        inflexion_spline.add(6 * (yValues.get(k) - 2 * v + w)
            / Math.pow(h.get(k), 2));

      }

      for (int k = 0; k <= inflexion_spline.size() - 2; k++) {

        // Are there unwanted inflexion points?
        if (((inflexion_spline.get(k) * inflexion_spline.get(k + 1)) < 0)
            && (inflexion_data.get(k) * inflexion_data.get(k + 1) > 0)) {
          // Yes, so increase the tension parameter
          p.set(k + 1, p.get(k + 1) + 1);

          // We have to do another iteration, to check whether the tension is
          // strong enough
          ready = false;

          // Threshold for tension (we do not want to have an endless loop)
          if (p.get(k + 1) > 5.0) {
            ready = true;
          }
        }
      }

      inflexion_data.clear();
      inflexion_spline.clear();

      computeParameters();

    } while (!ready);
  }

  @Override
  public void setYValues(List<Double> yValues) {
    this.yValues = yValues;
    initVectors();
    computeParameters();
    computeTensionFactor();
  }

}
