/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;

/**
 * Comparator for {@link IRuntimeConfiguration} instances, orders them by their
 * performance.
 * 
 * @author Roland Ewald
 */
public class RTCComparator implements Comparator<IRuntimeConfiguration> { // NOSONAR:is_not_serializable

  /** The list of applications. */
  private final List<IApplication> apps;

  /** The performance type for which to compare. */
  private final IPerformanceType perfType;

  /** The performance database. */
  private final IPerformanceDatabase perfDB;

  /**
   * The map that stores the average performance to determine the configuration
   * order.
   */
  private final Map<Long, Double> avgPerfMap = new HashMap<>();

  /** The map config_id => performance sum. */
  private final Map<Long, Double> perfSum = new HashMap<>();

  /** The map config_id => #applications. */
  private final Map<Long, Integer> perfCount = new HashMap<>();

  /**
   * Instantiates a new comparator for runtime configurations.
   * 
   * @param appList
   *          the list of available applications
   * @param pType
   *          the performance type by which shall be sorted
   * @param pDB
   *          the performance database for performance look-up
   */
  public RTCComparator(List<IApplication> appList, IPerformanceType pType,
      IPerformanceDatabase pDB) {
    apps = appList;
    perfType = pType;
    perfDB = pDB;
    init();
  }

  @Override
  public int compare(IRuntimeConfiguration o1, IRuntimeConfiguration o2) {
    Double o1Perf = Double.MAX_VALUE;
    Double o2Perf = Double.MAX_VALUE;

    if (avgPerfMap.containsKey(o1.getID())) {
      o1Perf = avgPerfMap.get(o1.getID());
    } else {
      avgPerfMap.put(o1.getID(), o1Perf);
    }

    if (avgPerfMap.containsKey(o2.getID())) {
      o2Perf = avgPerfMap.get(o2.getID());
    } else {
      avgPerfMap.put(o2.getID(), o2Perf);
    }

    return Double.compare(o1Perf, o2Perf);
  }

  /**
   * Initialises the comparator.
   */
  private void init() {
    for (IApplication app : apps) {
      registerPerformance(app, perfDB.getPerformance(app, perfType));
    }
    for (Long confID : perfCount.keySet()) {
      avgPerfMap.put(confID, perfSum.get(confID) / perfCount.get(confID));
    }
  }

  /**
   * Registers performance in both performance sum and count maps.
   * 
   * @param app
   *          the application in question
   * @param performance
   *          the corresponding performance of the predefined type
   */
  protected final void registerPerformance(IApplication app,
      IPerformance performance) {

    if (performance == null) {
      return;
    }

    long confID = app.getRuntimeConfiguration().getID();
    double perf = performance.getPerformance();

    if (perf < 0) {
      return;
    }

    if (!perfCount.containsKey(confID)) {
      perfCount.put(confID, 1);
      perfSum.put(confID, perf);
      return;
    }

    perfCount.put(confID, perfCount.get(confID) + 1);
    perfSum.put(confID, perfSum.get(confID) + perf);
  }

  /**
   * Gets the avg perf map.
   * 
   * @return the avg perf map
   */
  public Map<Long, Double> getAvgPerfMap() {
    return avgPerfMap;
  }

  /**
   * Gets the perf sum map.
   * 
   * @return the perf sum map
   */
  public Map<Long, Double> getPerfSum() {
    return perfSum;
  }

  /**
   * Gets the perf count map.
   * 
   * @return the perf count map
   */
  public Map<Long, Integer> getPerfCount() {
    return perfCount;
  }
}
