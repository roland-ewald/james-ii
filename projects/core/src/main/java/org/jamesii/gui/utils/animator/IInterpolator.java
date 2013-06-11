/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.animator;

/**
 * Interface that must be implemented by any interpolator used by an Animator.
 * 
 * @author Stefan Rybacki
 */
public interface IInterpolator {
  /**
   * Transforms the fraction value into an interpolated fraction value
   * 
   * @param frac
   *          fraction value between 0 and 1
   * @return interpolated fraction value (e.g. 1-frac for an inverted
   *         interpolation)
   */
  double interpolate(double frac);
}
