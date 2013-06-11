/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * Basic interface for ODE step controls.
 * 
 * @author Martin Kell
 */
public interface IOdeStepControl {

  /**
   * Computes the best next step in subject to parameters.
   * 
   * @param nextStep
   *          contains new Step after return (array for call-by-reference)
   * @param yold
   *          the old function value
   * @param order
   *          the order
   * @param ynew
   *          the new function value
   * @param error
   *          the error vector
   * 
   * @return true, if check step
   */
  boolean checkStep(double nextStep[], double ynew[], double yold[],
      double[] error, int order);

}
