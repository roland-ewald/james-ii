/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

import java.util.HashMap;
import java.util.Map;

import org.jamesii.gui.utils.ListenerSupport;

/**
 * Basic abstract implementation of an {@link ISeries} already implementing a
 * common set of methods. It also provides listener notification methods.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractSeries implements ISeries {
  /**
   * The list of registered listeners.
   */
  private final ListenerSupport<ISeriesListener> listeners =
      new ListenerSupport<>();

  /**
   * The name of the series.
   */
  private String name;

  private final Map<Integer, Number> minValues = new HashMap<>();

  private final Map<Integer, Number> maxValues = new HashMap<>();

  /**
   * Instantiates a new abstract series.
   */
  public AbstractSeries() {
    this(null);
  }

  /**
   * Instantiates a new abstract series.
   * 
   * @param name
   *          the name of the series
   */
  public AbstractSeries(String name) {
    this.name = name;
  }

  /**
   * Sets the name of the series.
   * 
   * @param name
   *          the new name of the series
   */
  protected final void setName(String name) {
    String oldName = this.name;
    this.name = name;
    fireNameChanged(oldName);
  }

  @Override
  public final Number getMinValue(int dimension) {
    Number m = minValues.get(dimension);
    if (m == null) {
      return minValue(dimension);
    }

    return m;
  }

  @Override
  public final Number getMaxValue(int dimension) {
    Number m = maxValues.get(dimension);
    if (m == null) {
      return maxValue(dimension);
    }

    return m;
  }

  /**
   * Method to provide the min value for a given dimension within the series.
   * BEWARE: don't call {@link #getMinValue(int)} method from within this method
   * as it will create infinite recursions.
   * 
   * @param dimension
   * @return the min value within the given dimension
   */
  protected abstract Number minValue(int dimension);

  /**
   * Method to provide the max value for a given dimension within the series.
   * BEWARE: don't call {@link #getMaxValue(int)} method from within this method
   * as it will create infinite recursions.
   * 
   * @param dimension
   * @return the max value within the given dimension
   */
  protected abstract Number maxValue(int dimension);

  @Override
  public final synchronized void addSeriesListener(ISeriesListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public final synchronized void removeSeriesListener(ISeriesListener listener) {
    listeners.removeListener(listener);
  }

  /**
   * Invoke this method if you added value to this series and want to notify
   * registered listeners.
   * 
   * @param index
   *          the index where a value was added to this series
   */
  protected final synchronized void fireValueAdded(int index) {
    for (ISeriesListener l : listeners) {
      if (l != null) {
        l.valueAdded(this, index);
      }
    }
  }

  /**
   * Invoke this method if you removed a value from this series and want to
   * notify registered listeners.
   * 
   * @param index
   *          the index where a value was removed from this series
   */
  protected final synchronized void fireValueRemoved(int index) {
    for (ISeriesListener l : listeners) {
      if (l != null) {
        l.valueRemoved(this, index);
      }
    }
  }

  /**
   * Helper method called when the name of this series was changed using
   * {@link #setName(String)}.
   * 
   * @param oldName
   *          the old name
   */
  private final synchronized void fireNameChanged(String oldName) {
    for (ISeriesListener l : listeners) {
      if (l != null) {
        l.nameChanged(this, oldName);
      }
    }
  }

  @Override
  public final String getName() {
    return name;
  }

  /**
   * Helper method called when the data of this series changed.
   */
  protected final synchronized void fireDataChanged() {
    for (ISeriesListener l : listeners) {
      if (l != null) {
        l.dataChanged(this);
      }
    }
  }

  public void setMin(int dimension, Number min) {
    minValues.put(dimension, min);
    fireDataChanged();
  }

  public void setMax(int dimension, Number max) {
    maxValues.put(dimension, max);
    fireDataChanged();
  }

}
