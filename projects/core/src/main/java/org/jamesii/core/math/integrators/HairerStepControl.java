/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * Simple version of Hairer's and Wanner's step control.
 * 
 * @author Martin Kell
 */
public class HairerStepControl implements IOdeStepControl {

  /** Smallest/biggest stepwidth, to avoid using too small steps. */
  static public double minStep = 1e-16; // NOSONAR

  /** The max step. */
  static public double maxStep = 1e1;// NOSONAR

  /** Max factor of step in/decrease (see Hairer). */
  private double facMax = 1.5;// NOSONAR

  /** Min factor of step in/decrease (see Hairer). */
  private double facMin = 0.5;// NOSONAR

  /** The factor. */
  private double fac = 0.9;// NOSONAR

  /** Tolerances (see Hairer). */
  private double absTol = 1e-10;// NOSONAR

  /** The relative tolerance. */
  private double relTol = 1e-10;// NOSONAR

  /**
   * Epsilon to compare the error with.
   */
  private static final double EPSILON = 1e-14;

  @Override
  public boolean checkStep(double nextStep[], double[] ynew, double[] yold,
      double error[], int order) {
    double err = 0;
    double n = ynew.length;
    for (int i = 0; i < n; i++) {
      double maxy = Math.max(Math.abs(yold[i]), Math.abs(ynew[i])); // anpassen!!

      double e = error[i] / (absTol * maxy + relTol);
      err += e * e;
    }
    if (n > 0) {
      err /= 1.0 * n;
    }
    err = Math.sqrt(err);

    if (Double.compare(err, EPSILON) <= 0) {
      nextStep[0] *= facMax;
      nextStep[0] = Math.max(nextStep[0], maxStep);

    } else {
      double newfak = fac * 1.0 / Math.pow(err, 1.0 / (order + 1.0));

      nextStep[0] *= Math.min(facMax, Math.max(facMin, newfak));
      nextStep[0] = Math.min(nextStep[0], maxStep);
      nextStep[0] = Math.max(nextStep[0], minStep);

      if (err > 1) {
        return false;
      }
    }
    return true;
  }

}
