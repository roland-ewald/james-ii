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
 * This class performs an exponential spline interpolation on given pairs of
 * values.
 * 
 * More information can be found in: Spaeth, Helmut: Eindimensionale
 * Spline-Interpolations-Algorithmen. R. Oldenbourg Verlag Muenchen, 1990, pp
 * 326-347.
 * 
 * @author Thomas Flisgen
 */

public class ExponentialSplineInterpolator implements IInterpolator {

  /** Automatic determination of tension parameters?. */
  private boolean automatic;

  /** x values used to interpolate. */
  private List<Double> xValues;

  /** y Values used to interpolate. */
  private List<Double> yValues;

  /** Parameters for interpolating function (see equation 7.1, p. 326) */
  private List<Double> lA = new ArrayList<>();

  /** Interpolation parameter B. */
  private List<Double> lB = new ArrayList<>();

  /** Interpolation parameter C. */
  private List<Double> lC = new ArrayList<>();

  /** Interpolation parameter D. */
  private List<Double> lD = new ArrayList<>();

  /** The tension parameter */
  private List<Double> p = new ArrayList<>();

  /** The p_init. */
  private double p_init;

  /** Coefficient alpha of LSE (see equation 7.32, p. 330) */
  private List<Double> alpha = new ArrayList<>();

  /** Coefficient beta of LSE (see equation 7.32, p. 330) */
  private List<Double> beta = new ArrayList<>();

  /** Length of intervals. */
  private List<Double> h = new ArrayList<>();

  /** Slope in each interval. */
  private List<Double> d = new ArrayList<>();

  /** Interpolation parameter a. */
  private List<Double> a = new ArrayList<>();

  /** Interpolation parameter b. */
  private List<Double> b = new ArrayList<>();

  /**
   * Set all tension parameters to a value defined by user.
   * 
   * @param tens
   *          the tensions
   */
  private void setAllTensionParam(double tens) {

    this.p_init = tens;

    for (int i = 0; i <= yValues.size() - 2; i++) {
      p.set(i, tens);
    }
  }

  /**
   * Initialize vectors.
   */
  private void initVectors() {
    for (int i = 0; i <= yValues.size() - 2; i++) {
      lA.add(i, 0.0);
      lB.add(i, 0.0);
      lC.add(i, 0.0);
      lD.add(i, 0.0);

      a.add(i, 0.0);
      b.add(i, 0.0);

      h.add(i, 0.0);
      d.add(i, 0.0);

      alpha.add(i, 0.0);
      beta.add(i, 0.0);
      p.add(i, 0.0);
    }
  }

  /**
   * Compute parameters.
   */
  private void computeParameters() {

    // Number of pairs to interpolate
    int n = xValues.size();

    // Precompute helping variables
    for (int i = 0; i <= n - 2; i++) {
      a.set(i, (Math.pow(p.get(i), 2) * Math.sinh(p.get(i)) / (Math.sinh(p
          .get(i)) - p.get(i))));
      b.set(
          i,
          (p.get(i) * Math.cosh(p.get(i)) - Math.sinh(p.get(i)))
              / (Math.sinh(p.get(i)) - p.get(i)));

      // (see equation 7.45, page 340)
      alpha.set(i, 1 / a.get(i));
      beta.set(i, b.get(i) * alpha.get(i));

      // (see equation 7.19, p. 328)
      lA.set(i, yValues.get(i));
      lB.set(i, yValues.get(i + 1));

      // Length between the intervals
      h.set(i, xValues.get(i + 1) - xValues.get(i));

      // Slopes in the interval
      d.set(i, ((yValues.get(i + 1) - yValues.get(i))) / h.get(i));
    }

    // (see equation 7.53, p. 343)
    List<Double> slopeOfSlope = new ArrayList<>();

    // Variables used to solve the tri-diagonal system
    List<Double> y = new ArrayList<>();
    List<Double> u = new ArrayList<>();
    List<Double> l = new ArrayList<>();

    // LU - Decomposition for a tri-diagonal matrix, A = L*U

    // L*U*x = b <=> L*y = b
    // U*x=y

    // Calculation of L and U, where the diagonals of the matrix are stored in
    // vector l and u
    u.add(0, beta.get(0) * h.get(0) + beta.get(1) * h.get(1));

    for (int i = 0; i <= n - 4; i++) {
      l.add(i, alpha.get(i + 1) * h.get(i + 1) / u.get(i));
      u.add(
          i + 1,
          beta.get(i + 1) * h.get(i + 1) + beta.get(i + 2) * h.get(i + 2)
              - l.get(i) * alpha.get(i + 1) * h.get(i + 1));
    }

    // Initialize slope_of_slope
    for (int k = 0; k <= n - 1; k++) {
      slopeOfSlope.add(k, 0.0);
    }

    // Forward elimination
    y.add(0, d.get(1) - d.get(0));

    for (int k = 1; k <= n - 3; k++) {
      y.add(k, d.get(k + 1) - d.get(k) - l.get(k - 1) * y.get(k - 1));
    }

    // Backward elimination
    slopeOfSlope.set(n - 2, y.get(n - 3) / u.get(n - 3));

    for (int k = n - 4; k >= 0; k -= 1) {
      slopeOfSlope.set(k + 1, (y.get(k) - (alpha.get(k + 1) * h.get(k + 1))
          * slopeOfSlope.get(k + 2))
          / u.get(k));
    }

    // Assign parameter (see equation 7.42, p. 340)
    for (int k = 0; k <= n - 2; k++) {
      lC.set(k, Math.pow(h.get(k), 2) * slopeOfSlope.get(k) / a.get(k));
      lD.set(k, Math.pow(h.get(k), 2) * slopeOfSlope.get(k + 1) / a.get(k));
    }

    y.clear();
    u.clear();
    l.clear();
    slopeOfSlope.clear();
  }

  // Help function for computation of equation 7.4, p. 326
  /**
   * Phi.
   * 
   * @param x
   *          the x
   * @param weight
   *          the weight
   * 
   * @return the double
   */
  private double phi(double x, double weight) {
    return (Math.sinh(weight * x) - x * Math.sinh(weight))
        / (Math.sinh(weight) - weight);
  }

  /**
   * Instantiates a new exponential spline interpolator.
   */
  public ExponentialSplineInterpolator() {
    this.p_init = .001;
    this.automatic = true;
  }

  /**
   * Instantiates a new exponential spline interpolator.
   * 
   * @param weight
   *          the weight
   */
  public ExponentialSplineInterpolator(double weight) {
    this.p_init = weight;
    this.automatic = false;
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
    initVectors();
    setAllTensionParam(p_init);
    computeParameters();
    if (this.automatic) {
      computeTensionFactor();
    }
  }

  /**
   * Change the weighting factor.
   * 
   * @param weight
   *          the weight
   */
  public void setWeight(double weight) {
    setAllTensionParam(weight);
    computeParameters();
    this.automatic = false;
  }

  /**
   * Read the weighting factor.
   * 
   * @return the weight
   */
  public double getWeight() {
    return this.p_init;
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

        // See equation 4.48, page 341
        inflexion_spline.add(this.lC.get(k + 1) * this.a.get(k + 1)
            / Math.pow(this.h.get(k + 1), 2));

      }

      for (int k = 0; k <= inflexion_spline.size() - 2; k++) {

        // Are there unwanted inflexion points?
        if (((inflexion_spline.get(k) * inflexion_spline.get(k + 1)) < 0)
            && (inflexion_data.get(k) * inflexion_data.get(k + 1) > 0)) {
          // Yes, so increase the tension parameter
          p.set(k + 1, p.get(k + 1) * 2);

          // We have to do another iteration, to check whether the tension is
          // strong enough
          ready = false;

          // Threshold for tension (we do not want to have an endless loop)
          if (p.get(k + 1) > 4.0) {
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
  public Double getOrdinateAtPosition(double abscisse) {

    double val;

    // Find the right interval
    int i = Arrays.binarySearch(xValues, abscisse, 0, xValues.size() - 1);

    // Compute the y value (see equation 7.3, p. 326)
    val =
        lA.get(i)
            * (xValues.get(i + 1) - abscisse)
            / (xValues.get(i + 1) - xValues.get(i))
            + lB.get(i)
            * (abscisse - xValues.get(i))
            / (xValues.get(i + 1) - xValues.get(i))
            + lC.get(i)
            * phi((xValues.get(i + 1) - abscisse)
                / (xValues.get(i + 1) - xValues.get(i)), p.get(i))
            + lD.get(i)
            * phi(
                (abscisse - xValues.get(i))
                    / (xValues.get(i + 1) - xValues.get(i)), p.get(i));

    // Return computed value
    return val;
  }
}
