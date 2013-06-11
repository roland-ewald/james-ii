/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * IOdeJacobian an interface for getting the Jacobian-Matrix df/dy and the
 * diff-vector df/dy of an ode y'=f(y,t). If the ode is autonomous, getDx
 * returns 0.
 * 
 * @author Martin Kell
 */
public interface IOdeJacobian {

  /**
   * Gets the dy.
   * 
   * @param y
   *          the y
   * @param t
   *          the t
   * 
   * @return df/dy as an nxn matrix
   */
  double[][] getDy(double y[], double t);

  /**
   * Gets the dx.
   * 
   * @param y
   *          the y
   * @param t
   *          the t
   * 
   * @return df/dx as an vector
   */
  double[] getDx(double y[], double t);

  /**
   * Setting a new ODE for approximating Jacobians.
   * 
   * @param ode
   *          the ODE system
   * 
   * @return false if setting not allowed or wrong ode (eg dimension mismatch)
   */
  boolean setOde(IOde ode);

  /**
   * Gets the ODE system.
   * 
   * @return the ode
   */
  IOde getOde();
}
