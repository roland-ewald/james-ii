/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import java.util.List;

import org.jamesii.core.util.misc.Arrays;

/**
 * This class executes a discrete interpolation by finding the function result
 * of the nearest value. This is the most simplest interpolation.
 * 
 * @author Stefan Leye
 */
public class DiscreteInterpolator implements IInterpolator {

  /** The given x values. */
  private List<Double> xValues;

  /** The given y values. */
  private List<Double> yValues;

  /**
   * Calculates the result of the discrete interpolation for a specific value.
   * 
   * @param abscisse
   *          the abscisse
   * 
   * @return the ordinate at position
   */
  @Override
  public Double getOrdinateAtPosition(double abscisse) {
    int i = Arrays.binarySearch(xValues, abscisse, 0, xValues.size() - 1);
    if (i >= xValues.size() - 1) {
      return yValues.get(i);
    }
    double lowerDist = abscisse - xValues.get(i);
    double upperDist = xValues.get(i + 1) - abscisse;
    if (lowerDist > upperDist) {
      return yValues.get(i + 1);
    }
    return yValues.get(i);
  }

  @Override
  public List<Double> getXValues() {
    return xValues;
  }

  @Override
  public void setXValues(List<Double> values) {
    xValues = values;
  }

  @Override
  public List<Double> getYValues() {
    return yValues;
  }

  @Override
  public void setYValues(List<Double> values) {
    yValues = values;
  }

}
