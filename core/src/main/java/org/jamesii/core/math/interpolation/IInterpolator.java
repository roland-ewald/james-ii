/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import java.util.List;

/**
 * Basic interface for interpolation algorithms.
 * 
 * @author Stefan Leye
 */
public interface IInterpolator {

  /**
   * Calculates the result of the interpolation function for a specific value.
   * 
   * @param abscisse
   *          x-value
   * 
   * @return y-value
   */
  Double getOrdinateAtPosition(double abscisse);

  /**
   * Returns the x-values used for interpolation.
   * 
   * @return the x values
   */
  List<Double> getXValues();

  /**
   * Set the x-values used for interpolation.
   * 
   * @param xValues
   *          the x values
   */
  void setXValues(List<Double> xValues);

  /**
   * Returns the y-values used for interpolation.
   * 
   * @return the y values
   */
  List<Double> getYValues();

  /**
   * Set the y-values used for interpolation.
   * 
   * @param yValues
   *          the y values
   */
  void setYValues(List<Double> yValues);
}
