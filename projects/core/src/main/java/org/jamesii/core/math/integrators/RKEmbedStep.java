/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * The Class RKEmbedStep realizes a Runge-Kutta one-step ODE solver.
 * 
 * @author Martin Kell
 */
public class RKEmbedStep implements IOdeOneStep {

  /** The A. */
  private double A[][];

  /** The b1. */
  private double b1[];

  /** The b2. */
  private double b2[];

  /** The c. */
  private double c[];

  /** The order. */
  private int order;

  /** The stages. */
  private int stages;

  /**
   * Instantiates a new rK embed step.
   * 
   * @param A
   *          the a
   * @param b1
   *          the b1
   * @param b2
   *          the b2
   * @param c
   *          the c
   * @param order
   *          the order
   */
  public RKEmbedStep(double A[][], double b1[], double b2[], double c[],
      int order) {
    this.A = A;
    this.b1 = b1;
    this.b2 = b2;
    this.c = c;
    this.order = order;
    this.stages = A.length;
  }

  @Override
  public double[][] doStep(double[] y, double t, double h, IOde ode) {

    // to be sure nothing gets wrong
    if (ode.getDimension() != y.length) {
      return null;
    }
    int n = y.length;

    // k(s,size(y,2)) = 0; % alle k_i auf 0
    // ret(size(y,1),size(y,2)) = 0;
    double ret[][] = new double[2][n];
    double k[][] = new double[stages][n];

    // k(1,:) = fhandle(x,y);
    // ret(:) = b(1,1)*k(1,:);
    // k[0] = ode.calculate(y, t);

    double yy[] = new double[n]; // a tmp-var
    // for j = 2:s
    for (int j = 0; j < stages; j++) {
      // yy = y;
      // for m = 1:(j-1)
      // yy = yy + h*A(j,m)*k(m);
      // end

      for (int i = 0; i < n; i++) {
        yy[i] = 0;
        for (int m = 0; m < j; m++) {
          yy[i] += A[j][m] * k[m][i];
        }
        yy[i] = y[i] + h * yy[i];
      }
      // k(j,:) = fhandle(x+c(j,1)*h, yy(:));
      k[j] = ode.calculate(yy, t + h * c[j]);
    }// end

    for (int i = 0; i < n; i++) {
      ret[0][i] = 0;
      ret[1][i] = 0;
      for (int j = 0; j < stages; j++) {
        ret[0][i] += b1[j] * k[j][i];
        // ret[1][i] += b2[j]*k[j][i];
        ret[1][i] += b2[j] * k[j][i];
      }
      ret[0][i] = h * ret[0][i] + y[i];
      ret[1][i] = h * ret[1][i] + y[i];
      ret[1][i] -= ret[0][i];
    }

    // end

    return ret;
  }

  @Override
  public int getOrder() {
    return order;
  }

}
