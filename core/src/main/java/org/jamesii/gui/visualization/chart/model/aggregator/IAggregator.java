/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model.aggregator;

import java.util.List;

/**
 * Basic aggregating interface to provide aggregated values for sets of values.
 * This can be used to implement {@link MaxAggregator} or {@link MinAggregator}
 * or any other aggregator.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IAggregator {

  /**
   * Aggregates a set of numbers.
   * 
   * @param set
   *          the set of numbers
   * 
   * @return the aggregated value
   */
  Number aggregate(List<Number> set);
}
