/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.statistics.univariate.Variance;

/**
 * Simple aggregator returning the variance of a set of numbers.
 * 
 * @author Stefan Rybacki
 * 
 */
public class VarianceAggregator implements IAggregator {

  @Override
  public Number aggregate(List<Number> set) {
    List<Double> l = new ArrayList<>(set.size());
    for (Number n : set) {
      l.add(n.doubleValue());
    }
    return Variance.variance(l);
  }

}
