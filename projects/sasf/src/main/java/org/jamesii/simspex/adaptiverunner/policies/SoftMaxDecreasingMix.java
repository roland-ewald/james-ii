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
 * and Mehryar Mohri (it's called SoftMix there).
 * 
 * Our implementation differs from the paper in adding an additional "1" (see
 * concrete formula below). This prevents a temperature of 0 in the 2nd round
 * (finishedPullsCount==1) and the temperature is a little bit higher in the
 * first 10 rounds. Otherwise the exploration phase would be extremely short.
 * The asymptotically behavior should not be affected much.
 * 
 * @author Rene Schulz
 * 
 */
public class SoftMaxDecreasingMix extends SoftMax {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6229012443459540407L;

  @Override
  protected double currentTemperature() {
    return (getTemperature() * Math.log10(getOverallPullCount()) + 1)
        / getOverallPullCount();
  }

}
