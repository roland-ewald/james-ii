/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * Step Control for constant steps.
 * 
 * @author Martin Kell
 */
public class ConstStepControl implements IOdeStepControl {

  @Override
  public boolean checkStep(double[] nextStep, double[] ynew, double[] yold,
      double[] error, int order) {
    // do nothing, and also don't change the step-size
    return true;
  }

}
