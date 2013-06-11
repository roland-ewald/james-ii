/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * 
 * This is an interface for an ordinary differential equation. An ordinary
 * equation depends on a set of variables that change according to some
 * parameter t.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public interface IOrdinaryDifferentialEquation {

  /**
   * Calculation of the equation
   * 
   * @param variables
   * @param t
   *          dependent variable (e.g., time)
   * @return return value
   */
  double calculate(double[] variables, double t);

  /**
   * Returns the index of the variable which is described by this ODE. Each
   * variable's changes can only be defined by one ODE, so this index has to be
   * unique
   * 
   * @return index of variable
   */
  int getVariableIndex();

}
