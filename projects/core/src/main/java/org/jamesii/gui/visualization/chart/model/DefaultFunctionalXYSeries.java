/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

import org.jamesii.core.util.misc.Pair;

/**
 * A default {@link IFunctionalXYSeries} implementation.
 * 
 * @author Stefan Rybacki
 */
public class DefaultFunctionalXYSeries extends DefaultXYSeries implements
    IFunctionalXYSeries {

  /** The last x value. */
  private Number lastValue;
  
  /**
   * Instantiates a new default functional xy series.
   * 
   * @param name
   *          the name of the series
   */
  public DefaultFunctionalXYSeries(String name) {
    super(name);
  }

  @Override
  public Number getValueAtX(Number x) {
    if (getMinValue(0).doubleValue() > x.doubleValue()) {
      return null;
    }
    if (getMaxValue(0).doubleValue() < x.doubleValue()) {
      return null;
    }
    if (getValueCount() <= 1) {
      return null;
    }

    // find data point x is in between
    int s = 0;
    int mi = 0;
    int ma = getValueCount() - 1;

    double xv = x.doubleValue();
    // using binary search
    do {
      double valueS = getValue(0, s).doubleValue();
      double valueE = getValue(0, s + 1).doubleValue();

      if (valueS <= xv && valueE >= xv) {
        // value should be between current value and previous value // TODO
        // sr137: use interpolator here
        double distance = (valueE - valueS);
        double alpha = (xv - valueS) / distance;

        return getValue(1, s).doubleValue() + alpha
            * (getValue(1, s + 1).doubleValue() - getValue(1, s).doubleValue());

      }

      if (valueS < xv) {
        mi = s;
        s = s + (ma - s) / 2;
      } else {
        int t = s;
        s = mi + (s - mi) / 2;
        ma = t;
      }
    } while (true);
  }

  @Override
  public synchronized void addValuePair(Pair<Number, Number> pair) {
    if (lastValue != null && lastValue.doubleValue() > pair.getFirstValue().doubleValue()) {
      throw new IllegalArgumentException(
          "Provided data pairs must have increasing x values!");
    }
    lastValue = pair.getFirstValue();
    super.addValuePair(pair);
  }

}
