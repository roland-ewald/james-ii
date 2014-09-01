/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

import java.util.Map;

/**
 * The Class to represent an autonomous linear ODE.
 * 
 * @author Martin Kell
 */
// TODO unimplemented methods from IOde
public class AutonomousLinearOde implements IOde, IOdeJacobian {

  /** The A matrix. */
  private double A[][];

  /**
   * The Constructor.
   * 
   * @param A
   *          this must be a square matrix
   */
  public AutonomousLinearOde(double A[][]) {
    int n = A.length;
    this.A = new double[n][n];
    for (int i = 0; i < n; i++) {
      System.arraycopy(A[i], 0, this.A[i], 0, n);
    }
  }

  @Override
  public double[] calculate(double[] y, double t) {
    int n = getDimension();
    double ret[] = new double[n];
    for (int i = 0; i < n; i++) {
      ret[i] = 0;
      for (int j = 0; j < n; j++) {
        ret[i] += A[i][j] * y[j];
      }
    }

    return ret;
  }

  @Override
  public int getDimension() {
    return A.length;
  }

  /**
   * Gets the matrix.
   * 
   * @return the matrix
   */
  public double[][] getMatrix() {
    int n = getDimension();
    double ret[][] = new double[n][n];
    for (int i = 0; i < n; i++) {
      System.arraycopy(A[i], 0, ret[i], 0, n);
    }
    return ret;
  }

  @Override
  public IOdeJacobian getJacobian() {
    return this;
  }

  @Override
  public double[][] getDy(double y[], double t) {
    return getMatrix();
  }

  @Override
  public double[] getDx(double y[], double t) {
    int n = getDimension();
    double ret[] = new double[n];
    for (int i = 0; i < n; i++) {
      ret[i] = 0;
    }
    return ret;
  }

  @Override
  public boolean setOde(IOde ode) {
    return false; // is never allowed here (A determines this ode)
  }

  @Override
  public IOde getOde() {
    return this;
  }

  @Override
  public double[] getInitialState() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, Integer> getVariableMapping() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, Integer> getResultMapping() {
    // TODO Auto-generated method stub
    return null;
  }

}
