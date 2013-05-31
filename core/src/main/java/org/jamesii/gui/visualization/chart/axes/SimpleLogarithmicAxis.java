/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

/**
 * Simple logaritmic scaled axis that scales logarithmic starting at
 * {@link #getMinimum()} until {@link #getMaximum()} between 0 and 1.
 * 
 * @author Stefan Rybacki
 */
public class SimpleLogarithmicAxis extends AbstractAxis {

  @Override
  public double transform(double value) {
    /*
     * so basically what is done here is to build a logarithmic scaled axis
     * since logarithmic scale axis usually start at 1 we need to map
     * getMinimum() -> getMaximum() to 1 -> (getMaximum()-getMinimum()). The
     * same conversion is done to the supplied value before it is mapped
     * logarithmicly.
     */
    double base = getMaximum() - getMinimum() + 1; // so that 0 maps to
                                                   // getMinimum() and 1 to
                                                   // getMaximum()

    // convert value into 1 -> base
    double transformedValue = (value - getMinimum()) + 1;

    if (transformedValue < 1) {
      return -1;
    }

    return Math.log(transformedValue) / Math.log(base);
  }

  @Override
  public double transformInv(double value) {
    double base = getMaximum() - getMinimum() + 1;
    return Math.pow(base, value) - 1 + getMinimum();
  }

}
