/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model.aggregator;

import java.util.List;

/**
 * Average aggregator implementing {@link IAggregator}.
 * 
 * @author Stefan Rybacki
 * 
 */
public class AvgAggregator implements IAggregator {

  @Override
  public Number aggregate(List<Number> set) {
    if (set == null || set.size() == 0) {
      return null;
    }

    double sum = 0;

    for (Number n : set) {
      sum += n.doubleValue();
    }

    return sum / set.size();
  }

}
