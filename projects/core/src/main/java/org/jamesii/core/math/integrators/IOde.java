/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

import java.util.Map;

/**
 * This is an interface to represent ODE-Systems.
 * 
 * @author Martin Kell
 * 
 */
public interface IOde {

  /**
   * Computes y'=f(y,t).
   * 
   * @param y
   *          vector of function values
   * @param t
   *          current parameter
   * 
   * @return the double[]
   */
  double[] calculate(double y[], double t);

  /**
   * Return df/dy ( y, t), if zero.
   * 
   * @return returns null if no Jacobian is available (compute numerically!?)
   */
  IOdeJacobian getJacobian();

  /**
   * Gets the dimension.
   * 
   * @return the dimension
   */
  int getDimension();

  /**
   * Returns the initial state of the ode (system).
   * 
   * @return
   */
  double[] getInitialState();

  /**
   * Returns the mapping from variable name to array index of the array y[]
   * given in the {@link #calculate(double[], double)} method.
   * 
   * @return the mapping
   */
  Map<String, Integer> getVariableMapping();

  /**
   * Returns the mapping from variable name to array index in the resulting
   * array from the {@link #calculate(double[], double)} method.
   * 
   * @return the mapping
   */
  Map<String, Integer> getResultMapping();

}
