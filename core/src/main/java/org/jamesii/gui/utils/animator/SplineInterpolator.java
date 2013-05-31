/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.animator;

/**
 * Spline interpolator converting a fraction between 0 and 1 into a spline curve
 * using incline and decline for start and end point to change the curve
 * behavior.
 * 
 * @author Stefan Rybacki
 * 
 */
public class SplineInterpolator implements IInterpolator {
  /**
   * incline of spline
   */
  private double acc = 0.5;

  /**
   * incline of spline
   */
  private double dec = 0.5;

  /**
   * Creates a spline interpolator implementing IInterpolator interface
   * 
   * @param acc
   *          incline of curve at start
   * @param dec
   *          incline of curve at end
   */
  public SplineInterpolator(double acc, double dec) {
    this.acc = acc;
    this.dec = dec;
  }

  @Override
  public final double interpolate(double frac) {
    // do a spline interpolation at frac
    /*
     * Hermite polynoms: H_{0}^3(t) = 1 - 3t^2 + 2t^3 H_{1}^3(t) = t - 2t^2 +
     * t^3 H_{2}^3(t) = -t^2 + t^3 H_{3}^3(t) = 3t^2 - 2t^3 = 1-H_{0}^3 (t)
     */

    double h30 = 1.0 - 3.0 * frac * frac + 2.0 * frac * frac * frac;
    double h31 = frac - 2.0 * frac * frac + frac * frac * frac;
    double h32 = -frac * frac + frac * frac * frac;
    double h33 = 1.0 - h30;

    /*
     * Spline interpolation with x0y0 = (0,0) and x1y1= (1,1) and m0=acc and
     * m1=dec
     */

    return acc * h31 + dec * h32 + h33;
  }

  /**
   * @return the acc
   */
  public double getAcc() {
    return acc;
  }

  /**
   * @param acc
   *          the acc to set
   */
  public void setAcc(double acc) {
    this.acc = acc;
  }

  /**
   * @return the dec
   */
  public double getDec() {
    return dec;
  }

  /**
   * @param dec
   *          the dec to set
   */
  public void setDec(double dec) {
    this.dec = dec;
  }

}