/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.animator;

/**
 * Discrete interpolator, only returning discrete values depending on the step
 * count specified in the constructor.
 * 
 * e.g. if 10 steps are specified the resulting factions the interpolator
 * returns are: 0.0 0.1 0.2 0.3 0.4 0.5 ... 0.8 0.9 1.0
 * 
 * if 5 steps are specified the result would be: 0.0 0.2 0.4 0.6 0.8 1.0
 * 
 * @author Stefan Rybacki
 * 
 */
public class DiscreteInterpolator implements IInterpolator {
  /**
   * discrete steps
   */
  private int steps;

  /**
   * Constructs a discrete interpolator
   * 
   * @param steps
   *          step count the interval 0 - 1 is divided into
   */
  public DiscreteInterpolator(int steps) {
    this.steps = steps;
  }

  @Override
  public final double interpolate(double frac) {
    return Math.floor(frac * steps) / steps;
  }

}
