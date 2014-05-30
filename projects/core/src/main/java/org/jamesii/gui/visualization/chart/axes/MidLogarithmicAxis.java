/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

/**
 * Logarithmic scaled axis that scales logarithmic starting in the middle of the
 * axis ({@link #getMinimum()} + {@link #getMaximum()} /2) between 0.5 and 0 and
 * 0.5 and 1.
 * 
 * @author Enrico Seib
 */
public class MidLogarithmicAxis extends AbstractAxis {

  @Override
  public double transform(double value) {

    double center = (getMaximum() - getMinimum()) / 2;
    double transformedValue = Math.abs(value - center - getMinimum()) + 1;

    if (value > (center + getMinimum())) {
      return 0.5 + ((Math.log(transformedValue) / Math.log(center + 1)) / 2);
    }

    return 0.5 - ((Math.log(transformedValue) / Math.log(center + 1)) / 2);

  }

  @Override
  public double transformInv(double value) {

    double center = (getMaximum() - getMinimum()) / 2;

    if (value > 0.5) {
      return center + (Math.pow(center + 1, (value - 0.5) * 2) - 1)
          + getMinimum();
    }

    return center - (Math.pow(center + 1, (0.5 - value) * 2) - 1)
        + getMinimum();

  }
}
