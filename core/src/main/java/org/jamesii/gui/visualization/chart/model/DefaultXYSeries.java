/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

import java.util.LinkedList;
import java.util.List;

import org.jamesii.core.util.misc.Pair;

/**
 * Default series implementation for two dimensional series.
 * 
 * @author Stefan Rybacki
 */
public class DefaultXYSeries extends AbstractSeries {
  /**
   * The series data.
   */
  private final List<Pair<Number, Number>> data = new LinkedList<>();

  /**
   * The cached min values.
   */
  private final Number[] cachedMin = new Number[2];

  /**
   * The cached max values.
   */
  private final Number[] cachedMax = new Number[2];

  /**
   * Instantiates a new default xy series.
   * 
   * @param name
   *          the name of the series, e.g. Temperature
   */
  public DefaultXYSeries(String name) {
    super(name);
    cachedMin[0] = Double.POSITIVE_INFINITY;
    cachedMin[1] = Double.POSITIVE_INFINITY;
    cachedMax[0] = Double.NEGATIVE_INFINITY;
    cachedMax[1] = Double.NEGATIVE_INFINITY;
  }

  @Override
  public int getDimensions() {
    return 2;
  }

  @Override
  public Number getValue(int dimension, int index) {
    Pair<Number, Number> pair = data.get(index);
    if (dimension == 0) {
      return pair.getFirstValue();
    }
    if (dimension == 1) {
      return pair.getSecondValue();
    }
    return null;
  }

  @Override
  public synchronized int getValueCount() {
    return data.size();
  }

  /**
   * Adds a value pair to the series.
   * 
   * @param pair
   *          the pair to add
   */
  public synchronized void addValuePair(Pair<Number, Number> pair) {
    data.add(pair);
    // check min and max values
    if (pair.getFirstValue().doubleValue() < cachedMin[0].doubleValue()) {
      cachedMin[0] = pair.getFirstValue();
    }
    if (pair.getSecondValue().doubleValue() < cachedMin[1].doubleValue()) {
      cachedMin[1] = pair.getSecondValue();
    }

    if (pair.getFirstValue().doubleValue() > cachedMax[0].doubleValue()) {
      cachedMax[0] = pair.getFirstValue();
    }
    if (pair.getSecondValue().doubleValue() > cachedMax[1].doubleValue()) {
      cachedMax[1] = pair.getSecondValue();
    }

    fireValueAdded(data.size() - 1);
  }

  /**
   * Adds the value pair to the series.
   * 
   * @param x
   *          the x value of the pair
   * @param y
   *          the y value of the pair
   * @return the generated pair
   */
  public synchronized Pair<Number, Number> addValuePair(Number x, Number y) {
    Pair<Number, Number> pair = null;
    addValuePair(pair = new Pair<>(x, y));
    return pair;
  }

  @Override
  protected Number maxValue(int dimension) {
    return cachedMax[dimension];
    /*
     * // goes in 10th steps double max =
     * (Math.floor(cachedMax[dimension].doubleValue() / 10) * 10 + 10); // also
     * adds 10% margin depending on actual max value return max + Math.abs(max)
     * * 0.1;
     */
  }

  @Override
  protected Number minValue(int dimension) {
    return cachedMin[dimension];
    // return Math.floor(cachedMin[dimension].doubleValue() / 10) *
    // 10;
  }

  /**
   * Cuts old values so that the series holds at most maxValues values.
   * 
   * @param maxValues
   *          the maximum amount of values to cut to
   */
  public void cutToValueCount(int maxValues) {
    for (int i = data.size() - maxValues; i >= 0; i--) {
      data.remove(i);
    }
    updateCache();
    fireDataChanged();
  }

  private void updateCache() {
    cachedMin[0] = Double.POSITIVE_INFINITY;
    cachedMin[1] = Double.POSITIVE_INFINITY;
    cachedMax[0] = Double.NEGATIVE_INFINITY;
    cachedMax[1] = Double.NEGATIVE_INFINITY;
    for (Pair<Number, Number> pair : data) {
      // check min and max values
      if (pair.getFirstValue().doubleValue() < cachedMin[0].doubleValue()) {
        cachedMin[0] = pair.getFirstValue();
      }
      if (pair.getSecondValue().doubleValue() < cachedMin[1].doubleValue()) {
        cachedMin[1] = pair.getSecondValue();
      }

      if (pair.getFirstValue().doubleValue() > cachedMax[0].doubleValue()) {
        cachedMax[0] = pair.getFirstValue();
      }
      if (pair.getSecondValue().doubleValue() > cachedMax[1].doubleValue()) {
        cachedMax[1] = pair.getSecondValue();
      }
    }
  }
}
