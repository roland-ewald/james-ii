/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Simple {@link IAxis} wrapper providing a different min/max behavior, where
 * those values only change in fixed scaled steps dependently from the actual
 * min and max values.
 * 
 * @author Stefan Rybacki
 * 
 */
public class SteppingAxis implements IAxis {

  /**
   * The previous maximum value.
   */
  private double prevMax;

  /**
   * The previous minimum value.
   */
  private double prevMin;

  /**
   * The scale for maximum value.
   */
  private double scaleMax;

  /**
   * The scale for minimum value.
   */
  private double scaleMin;

  /**
   * The wrapped axis.
   */
  private IAxis axis;

  /**
   * Instantiates a new stepping axis.
   * 
   * @param axis
   *          the axis to wrap
   * @param addedScaleMin
   *          the added scale min
   * @param addedScaleMax
   *          the added scale max
   */
  public SteppingAxis(IAxis axis, double addedScaleMin, double addedScaleMax) {
    super();
    this.scaleMax = 1d + addedScaleMax;
    this.scaleMin = 1d + addedScaleMin;
    this.axis = axis;
  }

  @Override
  public double getMaximum() {
    double max = axis.getMaximum();
    double min = axis.getMinimum();
    double diff = max - min;

    if (max >= prevMax) {
      prevMax = min + diff * scaleMax;
    }

    return prevMax;
  }

  @Override
  public double getMinimum() {
    double max = axis.getMaximum();
    double min = axis.getMinimum();
    double diff = max - min;

    if (min <= prevMin) {
      prevMin = max - diff * scaleMin;
    }

    return prevMin;
  }

  @Override
  public void addAxisListener(IAxisListener listener) {
    axis.addAxisListener(listener);
  }

  @Override
  public List<Double> getTickMarks(int count) {
    return axis.getTickMarks(count);
  }

  @Override
  public void removeAxisListener(IAxisListener listener) {
    axis.removeAxisListener(listener);
  }

  @Override
  public void setMaximum(double max) {
    axis.setMaximum(max);
  }

  @Override
  public void setMinimum(double min) {
    axis.setMinimum(min);
  }

  @Override
  public double transform(double value) {
    return axis.transform(value);
  }

  @Override
  public double transformInv(double value) {
    return axis.transformInv(value);
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener l) {
    axis.addPropertyChangeListener(l);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
    axis.removePropertyChangeListener(l);
  }

}
