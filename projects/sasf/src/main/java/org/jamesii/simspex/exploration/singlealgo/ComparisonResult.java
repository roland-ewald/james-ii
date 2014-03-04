/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.util.ParameterBlocks;

/**
 * Contains simple statistical information.
 * 
 * @author Roland Ewald
 */
public class ComparisonResult {

  /** The average performance map. */
  private final Map<Long, Double> avgPerfMap = new HashMap<>();

  /** The map config_id => performance sum. */
  private final Map<Long, Double> perfSum = new HashMap<>();

  /** The map config_id => #applications. */
  private final Map<Long, Integer> perfCount = new HashMap<>();

  /** The comparator for runtime configurations (contains old data). */
  private final RTCComparator rtcComparator;

  /** The params config map. */
  private Map<Long, ParameterBlock> configParamMap;

  /**
   * Instantiates a new comparison result.
   * 
   * @param rtcComp
   *          the comparator for runtime configurations
   */
  public ComparisonResult(RTCComparator rtcComp,
      Map<Long, ParameterBlock> confPMap) {
    rtcComparator = rtcComp;
    configParamMap = confPMap;
  }

  /**
   * Registers newly recorded performance.
   * 
   * @param configID
   *          the id of the runtime configuration
   * @param performance
   *          the performance of the runtime configuration in this setting
   */
  public void registerPerformance(long configID, double performance) {
    double sum = 0;
    int count = 0;
    if (perfSum.containsKey(configID)) {
      sum = perfSum.get(configID);
      count = perfCount.get(configID);
    }
    sum += performance;
    count++;
    perfSum.put(configID, sum);
    perfCount.put(configID, count);
  }

  /**
   * Starts analysis.
   */
  public SimpleStatistics analyze(Set<Long> configsWAlgo) {

    calculateAveragePerformance();

    // Collect comparison data
    PerfStatsAlgo comp = null, compOld = null, bestNew = null, bestOld = null, worstNew =
        null, worstOld = null;
    double bestPerf = Double.MAX_VALUE, worstPerf = 0;
    long bestID = -1, worstID = -1;
    Long comparisonConfigID = null;

    for (Long configID : avgPerfMap.keySet()) {
      // FIXME: Use search sub-block instead
      if (!configsWAlgo.contains(configID)) {
        comp =
            new PerfStatsAlgo(avgPerfMap.get(configID), configID,
                ParameterBlocks.toUniqueString(configParamMap.get(configID)));
        comparisonConfigID = configID;
        continue;
      }
      double perf = avgPerfMap.get(configID);
      if (bestPerf > perf) {
        bestPerf = perf;
        bestID = configID;
      }
      if (worstPerf < perf) {
        worstPerf = perf;
        worstID = configID;
      }
    }
    bestNew =
        new PerfStatsAlgo(bestPerf, bestID,
            ParameterBlocks.toUniqueString(configParamMap.get(bestID)));
    worstNew =
        new PerfStatsAlgo(worstPerf, worstID,
            ParameterBlocks.toUniqueString(configParamMap.get(worstID)));

    bestPerf = Double.MAX_VALUE;
    worstPerf = 0;
    bestID = -1;
    worstID = -1;
    Map<Long, Double> oldAvgPerfMap = rtcComparator.getAvgPerfMap();
    for (Entry<Long, Double> configPerf : oldAvgPerfMap.entrySet()) {
      if (!configsWAlgo.contains(configPerf.getKey())) {
        continue;
      }
      double perf = oldAvgPerfMap.get(configPerf.getKey());
      if (bestPerf > perf) {
        bestPerf = perf;
        bestID = configPerf.getKey();
      }
      if (worstPerf < perf) {
        worstPerf = perf;
        worstID = configPerf.getKey();
      }
    }

    // Create old comparison data
    if (comp != null) {
      double compOldPerf = oldAvgPerfMap.get(comparisonConfigID);
      compOld =
          new PerfStatsAlgo(compOldPerf, comparisonConfigID,
              ParameterBlocks.toUniqueString(configParamMap
                  .get(comparisonConfigID)));
    } else {
      comp = new PerfStatsAlgo(Double.MAX_VALUE, -1L, "");
      compOld = new PerfStatsAlgo(Double.MAX_VALUE, -1L, "");
    }

    bestOld =
        new PerfStatsAlgo(bestPerf, bestID,
            ParameterBlocks.toUniqueString(configParamMap.get(bestID)));
    worstOld =
        new PerfStatsAlgo(worstPerf, worstID,
            ParameterBlocks.toUniqueString(configParamMap.get(worstID)));

    return new SimpleStatistics(comp, compOld, bestNew, bestOld, worstNew,
        worstOld);
  }

  private void calculateAveragePerformance() {
    // Calculate average performance
    for (long configID : perfSum.keySet()) {
      avgPerfMap.put(configID, perfSum.get(configID) / perfCount.get(configID));
    }
  }

  /**
   * Gets the avg perf map.
   * 
   * @return the avgPerfMap
   */
  public Map<Long, Double> getAvgPerfMap() {
    return avgPerfMap;
  }

  /**
   * Gets the perf sum.
   * 
   * @return the perfSum
   */
  public Map<Long, Double> getPerfSum() {
    return perfSum;
  }

  /**
   * Gets the perf count.
   * 
   * @return the perfCount
   */
  public Map<Long, Integer> getPerfCount() {
    return perfCount;
  }

  /**
   * Gets the rtc comparator.
   * 
   * @return the rtcComparator
   */
  public RTCComparator getRTCComparator() {
    return rtcComparator;
  }
}
