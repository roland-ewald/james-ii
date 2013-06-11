/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.gui.utils.ListenerSupport;

/**
 * Abstract class that can be used to implement own {@link IAxis} classes.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractAxis implements IAxis {
  /**
   * the min value of this axis
   */
  private double min = Double.NEGATIVE_INFINITY;

  /**
   * the max value of this axis
   */
  private double max = Double.POSITIVE_INFINITY;

  /**
   * support for property change events
   */
  private final PropertyChangeSupport propertyChange =
      new PropertyChangeSupport(this);

  /**
   * listener support
   */
  private final ListenerSupport<IAxisListener> listeners =
      new ListenerSupport<>();

  /**
   * The minimal distance between min and max value in case min and max value
   * are the same.
   */
  private final double minimalDistance;

  /**
   * Instantiates a new abstract axis.
   */
  public AbstractAxis() {
    this(0.1);
  }

  /**
   * Instantiates a new abstract axis.
   * 
   * @param minimalDistance
   *          the minimal distance between min and max in case min and max are
   *          equal
   */
  public AbstractAxis(double minimalDistance) {
    super();
    this.minimalDistance = minimalDistance;
  }

  @Override
  public double getMaximum() {
    if (Double.compare(min, max) == 0) {
      return max + minimalDistance / 2d;
    }

    return max;
  }

  @Override
  public double getMinimum() {
    if (Double.compare(min, max) == 0) {
      return min - minimalDistance / 2d;
    }
    return min;
  }

  @Override
  public final void setMaximum(double max) {
    if (max < min) {
      min = max;
    }
    double old = this.max;
    this.max = max;

    propertyChange.firePropertyChange("maximum", old, max);
    fireMaximumChanged(this, old);
  }

  @Override
  public final void setMinimum(double min) {
    if (min > max) {
      max = min;
    }

    double old = this.min;
    this.min = min;

    propertyChange.firePropertyChange("minimum", old, min);
    fireMinimumChanged(this, old);
  }

  @Override
  public final void addPropertyChangeListener(PropertyChangeListener l) {
    propertyChange.addPropertyChangeListener(l);
  }

  @Override
  public final void removePropertyChangeListener(PropertyChangeListener l) {
    propertyChange.removePropertyChangeListener(l);
  }

  @Override
  public final List<Double> getTickMarks(int count) {
    List<Double> result = new ArrayList<>();

    for (int i = 0; i <= count; i++) {
      result.add(Double.valueOf(transformInv(i * 1d / count)));
    }

    return result;
  }

  /**
   * Sets the minimum and maximum value at the same time. mini must be less than
   * maxi
   * 
   * @param mini
   *          the new min value
   * @param maxi
   *          the new max value
   */
  public final void setMinimumMaximum(double mini, double maxi) {
    if (mini >= maxi) {
      throw new IllegalArgumentException("min must be < max");
    }

    double oldMax = max;
    double oldMin = min;
    min = mini;
    max = maxi;

    propertyChange.firePropertyChange("min", oldMin, min);
    propertyChange.firePropertyChange("max", oldMax, max);
    fireMaximumChanged(this, oldMax);
    fireMinimumChanged(this, oldMin);
  }

  @Override
  public void addAxisListener(IAxisListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public void removeAxisListener(IAxisListener listener) {
    listeners.removeListener(listener);
  }

  /**
   * Use this method to notify registered listeners of changes to an
   * {@link IAxis}s maximum value.
   * 
   * @param axis
   *          the axis that changed
   * @param old
   *          the old maximum value
   */
  protected final synchronized void fireMaximumChanged(IAxis axis, double old) {
    for (IAxisListener l : listeners) {
      if (l != null) {
        l.maximumChanged(axis, old);
      }
    }
  }

  /**
   * Use this method to notify registered listeners of changes to an
   * {@link IAxis}s minimum value.
   * 
   * @param axis
   *          the axis that changed
   * @param old
   *          the old minimum value
   */
  protected final synchronized void fireMinimumChanged(IAxis axis, double old) {
    for (IAxisListener l : listeners) {
      if (l != null) {
        l.minimumChanged(axis, old);
      }
    }
  }

  /**
   * Use this method to notify registered listeners of changes to an
   * {@link IAxis}s state.
   * 
   * @param axis
   *          the axis that changed
   */
  protected final synchronized void fireStateChanged(IAxis axis) {
    for (IAxisListener l : listeners) {
      if (l != null) {
        l.stateChanged(axis);
      }
    }
  }

}
