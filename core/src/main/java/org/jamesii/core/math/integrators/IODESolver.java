/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

import java.util.List;

/**
 * Basic interface for ODE solvers.
 * 
 * @author Stefan Leye
 */
public interface IODESolver {

  /**
   * Gets the interpolation values.
   * 
   * @return the interpolation values
   */
  List<Double> getInterpolationValues();

  /**
   * Gets the ode system.
   * 
   * @return the ode system
   */
  IOde getOdeSystem();

  /**
   * Core function. Executes the (numerical) integration to get a solution for
   * the ODE.
   */
  void solveODESystem();

  /**
   * Calculates the solution for the next step.
   */
  void solveNextStep();

  /**
   * Get the trace of the ODE's. Should be called after solveODESystem() (since
   * the trace is calculated during this method)!
   * 
   * @return returns the solution of the {@link IOde} as a list of state
   *         snapshots that correspond to the interpolation values
   */
  List<double[]> getOdeSystemTrace();
}
