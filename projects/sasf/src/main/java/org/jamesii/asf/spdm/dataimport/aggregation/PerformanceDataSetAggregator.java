/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport.aggregation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.dataimport.ProblemPerformanceTuple;

/**
 * Aggregates performance data set.
 * 
 * @see PerformanceDataSet
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PerformanceDataSetAggregator<T extends ProblemPerformanceTuple> {

  /** The performance data set to be aggregated. */
  private final PerformanceDataSet<T> perfDataSet;

  /** Whether or not to maximize the performance in the aggregated data set. */
  private final boolean maxPerformance;

  /**
   * A contains a black-list of feature names (or substrings). If a feature name
   * contains any string in this list, it shall be filtered.
   */
  private final List<String> filterFeatures;

  /**
   * Instantiates a new performance data set aggregator.
   * 
   * @param performanceDataSet
   *          the performance data set to be aggregated
   * @param featuresToFilter
   *          the features to filter
   * @param maximizePerformance
   *          the maximize performance flag
   */
  public PerformanceDataSetAggregator(PerformanceDataSet<T> performanceDataSet,
      List<String> featuresToFilter, boolean maximizePerformance) {
    perfDataSet = performanceDataSet;
    maxPerformance = maximizePerformance;
    filterFeatures = new ArrayList<>(featuresToFilter);
  }

  /**
   * Instantiates a new performance data set aggregator.
   * 
   * @param performanceDataSet
   *          the performance data set
   */
  public PerformanceDataSetAggregator(PerformanceDataSet<T> performanceDataSet) {
    this(performanceDataSet, Collections.<String> emptyList(),
        performanceDataSet.getMetaData().isMaximizePerformance());
  }

  /**
   * Aggregates performance data set.
   * 
   * @return the performance data set
   */
  public PerformanceDataSet<PerformanceTuple> aggregate() {
    PerformanceDataSet<PerformanceTuple> newDataSet =
        new PerformanceDataSet<>(
            aggregatePerformanceTuples(perfDataSet.getInstances()),
            maxPerformance);
    return newDataSet;
  }

  /**
   * Aggregate performance tuples.
   * 
   * @param instances
   *          the instances
   * @return the list
   */
  public List<PerformanceTuple> aggregatePerformanceTuples(List<T> instances) {
    return new ArrayList<PerformanceTuple>(instances);
  }

  public List<String> getFilterFeatures() {
    return new ArrayList<>(filterFeatures);
  }
}
