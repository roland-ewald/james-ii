/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.animator;

/**
 * Linear interpolator.
 * 
 * @author Stefan Rybacki
 * 
 */
public class LinearInterpolator implements IInterpolator {

  @Override
  public final double interpolate(double frac) {
    return frac;
  }

}
