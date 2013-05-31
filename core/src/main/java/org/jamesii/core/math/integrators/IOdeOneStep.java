/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * Interface for a "one step function" for ode-solver (e.g., rkv-step,
 * implicit-step (rosenbrock,...)).
 * 
 * @author Martin Kell
 * 
 */
public interface IOdeOneStep {

  /**
   * The step-function. The step-function depends on last (t,y), current step,
   * and the ODE. If the method is not embedded it returns double[1][n], else
   * double[2][n]!
   * 
   * @param y
   *          the last output vector
   * @param t
   *          the t last time step value
   * @param h
   *          the h step size
   * @param ode
   *          the ode
   * 
   * @return the double[][]
   */
  double[][] doStep(double y[], double t, double h, IOde ode);

  /**
   * Get the order.
   * 
   * @return consistency order of the step function (should be at least 1!)
   */
  int getOrder();
}
