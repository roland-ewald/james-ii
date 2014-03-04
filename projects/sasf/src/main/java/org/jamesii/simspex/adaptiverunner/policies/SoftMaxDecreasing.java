/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * Similar to (@link SoftMax) but the temperature decreases with time. See:
 * "Multi-Armed Bandit Algorithms and Empirical Evaluation" by Joann`es Vermorel
 * and Mehryar Mohri.
 * 
 * 
 * @author Rene Schulz
 * 
 */
public class SoftMaxDecreasing extends SoftMax {

  /** Serialisation ID. */
  private static final long serialVersionUID = -5502955769972741983L;

  @Override
  protected double currentTemperature() {
    return getTemperature() / getOverallPullCount();
  }

}
