/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model.aggregator;

import java.util.List;

/**
 * Maximum aggregator implementing {@link IAggregator}.
 * 
 * @author Stefan Rybacki
 * 
 */
public class MaxAggregator implements IAggregator {

  @Override
  public Number aggregate(List<Number> set) {
    if (set == null || set.size() == 0) {
      return null;
    }

    double max = Double.NEGATIVE_INFINITY;

    for (Number n : set) {
      if (n.doubleValue() > max) {
        max = n.doubleValue();
      }
    }

    return max;
  }

}
