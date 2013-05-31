/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.animator;

/**
 * Interpolator that interpolates back and forth once using the specified
 * interpolator.
 * 
 * @author Stefan Rybacki
 * 
 */
public class AlternatingInterpolator implements IInterpolator {
  /**
   * internally used interpolator
   */
  private IInterpolator interpolator;

  /**
   * Creates an alternating interpolator that used the specified interpolator
   * 
   * @param interpolator
   *          the interpolator used during alternation
   */
  public AlternatingInterpolator(IInterpolator interpolator) {
    this.interpolator = interpolator;
  }

  @Override
  public final double interpolate(double frac) {
    if (frac <= 0.5) {
      return interpolator.interpolate(frac * 2);
    }
    return interpolator.interpolate(2 - frac * 2);
  }

}
