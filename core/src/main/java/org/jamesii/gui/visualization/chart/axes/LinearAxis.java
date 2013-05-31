/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

/**
 * Simple implementation of a linear axis.
 * 
 * @author Stefan Rybacki
 */
public class LinearAxis extends AbstractAxis {

  @Override
  public double transform(double value) {
    return (value - getMinimum()) / (getMaximum() - getMinimum());
  }

  @Override
  public double transformInv(double tValue) {
    return tValue * (getMaximum() - getMinimum()) + getMinimum();
  }

}
