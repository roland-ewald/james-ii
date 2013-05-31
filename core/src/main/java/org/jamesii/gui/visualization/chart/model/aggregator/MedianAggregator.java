/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model.aggregator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Median aggregator implementing {@link IAggregator}.
 * 
 * @author Stefan Rybacki
 * 
 */
public class MedianAggregator implements IAggregator {

  @Override
  public Number aggregate(List<Number> set) {
    // first sort set and then take center element
    Collections.sort(set, new Comparator<Number>() {

      @Override
      public int compare(Number o1, Number o2) {
        return (int) Math.signum(o1.doubleValue() - o2.doubleValue());
      }

    });

    return set.get(set.size() / 2);
  }

}
