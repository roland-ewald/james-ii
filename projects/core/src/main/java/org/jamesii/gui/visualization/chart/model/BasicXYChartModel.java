/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a basic {@link IChartModel} implementation which can be
 * used in 2-dimensional charts. This class supports a different number of
 * series which can be added using {@link #addSeries(ISeries)} as well as
 * removed using {@link #removeSeries(ISeries)}. Where values can be added to
 * any series separately. The model only supports 2 groups where the groups are
 * bound to the dimensions.
 * 
 * @author Stefan Rybacki
 */
public class BasicXYChartModel extends AbstractChartModel implements
    ISeriesListener {

  /**
   * The list of registered serieses.
   */
  private final List<ISeries> serieses = new ArrayList<>();

  @Override
  public int getDimensions() {
    return 2;
  }

  @Override
  public int getGroup(ISeries seriesIndex, int dimension) {
    return dimension;
  }

  @Override
  public String getGroupName(int group) {
    switch (group) {
    case 0:
      return "X";
    case 1:
      return "Y";
    }
    return null;
  }

  @Override
  public synchronized Number getMaxValue(int group) {
    double max = Double.NEGATIVE_INFINITY;
    for (ISeries s : serieses) {
      max = Math.max(max, s.getMaxValue(group).doubleValue());
    }
    if (max == Double.NEGATIVE_INFINITY) {
      return 0;
    }
    return max;
  }

  @Override
  public synchronized Number getMinValue(int group) {
    double min = Double.POSITIVE_INFINITY;
    for (ISeries s : serieses) {
      min = Math.min(min, s.getMinValue(group).doubleValue());
    }
    if (min == Double.POSITIVE_INFINITY) {
      return 0;
    }
    return min;
  }

  @Override
  public synchronized int getSeriesCount() {
    return serieses.size();
  }

  @Override
  public Number getValue(ISeries series, int dimension, int valueIndex) {
    if (!serieses.contains(series)) {
      return null;
    }

    return series.getValue(dimension, valueIndex);
  }

  @Override
  public synchronized int getValueCount(ISeries series) {
    return series.getValueCount();
  }

  /**
   * Adds a new series to the chart model. If the series is already registered
   * no operation is performed.
   * 
   * @param s
   *          the series to add
   */
  public synchronized void addSeries(ISeries s) {
    if (s == null) {
      throw new InvalidModelException("series can't be null");
    }
    if (!serieses.contains(s)) {
      serieses.add(s);
      s.addSeriesListener(this);
      fireSeriesAdded(s);
    }
  }

  /**
   * Removes a previously added series from this model. If the series is not
   * registered in this model no operation is performed.
   * 
   * @param s
   *          the series to remove
   */
  public synchronized void removeSeries(ISeries s) {
    int i = serieses.indexOf(s);
    if (i >= 0) {
      s.removeSeriesListener(this);
      serieses.remove(i);
      fireSeriesRemoved(s);
    }
  }

  @Override
  public synchronized void valueAdded(ISeries s, int index) {
    fireValueAdded(s, index);
  }

  @Override
  public synchronized void valueRemoved(ISeries s, int index) {
    fireValueRemoved(s, index);
  }

  /**
   * Removes all series at once.
   */
  public synchronized void removeAllSeries() {
    for (ISeries s : serieses) {
      s.removeSeriesListener(this);
    }
    serieses.clear();
    fireDataChanged();
  }

  @Override
  public void nameChanged(ISeries series, String oldName) {
  }

  @Override
  public String getSeriesName(ISeries series) {
    return series.getName();
  }

  @Override
  public void dataChanged(ISeries series) {
    int i = serieses.indexOf(series);
    if (i >= 0) {
      fireDataChanged();
    }
  }

  @Override
  public ISeries getSeries(int index) {
    return serieses.get(index);
  }
}
