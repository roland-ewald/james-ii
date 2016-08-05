/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.statistics.univariate.Variance;
import org.jamesii.core.util.collection.ArrayMap;

/**
 * Container for records related to time an observer should accumulate over the
 * course of an experiment observation. When notified on a regular basis via
 * {@link #recordTime(Double, Long)}, this class records
 * <ul>
 * <li>the simulation time, i.e. the value of the first parameter
 * <li>the wall clock time elapsed since the first notification
 * <li>the number of the iteration, i.e. the value of the second parameter, if
 * it is not null
 * <li>the number of iterations per unit of wall clock time, if the relevant
 * constructor flag is set and the former value is not null
 * <li>the number of iterations per unit of simulation time, if the relevant
 * constructor flag is set and the former value is not null.
 * </ul>
 *
 * @author Arne Bittig
 * @param <SID>
 *          (optional) Snapshot ID type
 */
public class TimeRecorder<SID> implements java.io.Serializable {

  private static final long serialVersionUID = 6609432291248162039L;

  private final boolean recordNumItByWcTime;

  private final boolean recordNumItBySimTime;

  /** Recorded simulation time */
  private final List<Double> simTimeColumn = new ArrayList<>();

  /** Recorded wall clock time */
  private final List<Integer> wcTimeColumn = new ArrayList<>();

  /** Recorded number of iterations */
  private final List<Long> numItColumn = new ArrayList<>();

  /** Recorded snapshot IDs */
  private final List<SID> sidColumn = new ArrayList<>();

  /** Number of iterations by elapsed wall clock time since last update */
  private final List<Double> numItByWcTime = new ArrayList<>();

  /** Number of iterations by elapsed simulation time since last update */
  private final List<Double> numItBySimTime = new ArrayList<>();

  /** System time at which observer was first notified */
  private long wcStartTime = Long.MAX_VALUE;

  private long lastNumIt = -1;

  private long wcLastTime = -1;

  private double simLastTime = Double.NEGATIVE_INFINITY;

  /**
   * Time recorder constructor (the flags are only relevant if the iteration
   * count is passed regularily to this recorder along with the simulation time
   * in {@link #recordTime(Double, Long)})
   *
   * @param rcrdItByWallTime
   *          Flag whether to record number of iterations by wall clock time
   * @param rcrdItBySimTime
   *          Flag whether to record number of iterations by simulation time
   */
  public TimeRecorder(Boolean rcrdItByWallTime, Boolean rcrdItBySimTime) {
    this.recordNumItByWcTime = rcrdItByWallTime != null && rcrdItByWallTime;
    this.recordNumItBySimTime = rcrdItBySimTime != null && rcrdItBySimTime;
  }

  /**
   * Notify recorder of next data point to record
   *
   * @param time
   *          Simulation time
   * @param iteration
   *          Number of simulation step
   * @param snapshotID
   *          Snapshot ID (e.g. simulation time with same state)
   */
  public void recordTime(Double time, Long iteration, SID snapshotID) {
    simTimeColumn.add(time);
    long currWCTime = System.currentTimeMillis();
    wcTimeColumn.add((int) (currWCTime - wcStartTime));
    recordIteration(iteration, currWCTime, time);
    recordSnapshotID(snapshotID);
  }

  private void recordIteration(Long iteration, long currWCTime,
      Double simTime) {
    if (iteration != null) {
      numItColumn.add(iteration);

      if (recordNumItByWcTime || recordNumItBySimTime) {
        if (recordNumItByWcTime) {
          recordNumItByX(iteration, (double) (currWCTime - wcLastTime),
              numItByWcTime);
        }
        if (recordNumItBySimTime) {
          recordNumItByX(iteration, simTime - simLastTime, numItBySimTime);
        }

        lastNumIt = iteration;
        wcLastTime = currWCTime;
        simLastTime = simTime;
      }
    }
  }

  private void recordNumItByX(Long currNumIt, Double x, List<Double> itByX) {
    if (lastNumIt < 0) {
      itByX.add(Double.NaN);
    } else {
      itByX.add((currNumIt - lastNumIt) / x);
    }
  }

  private void recordSnapshotID(SID snapshotID) {
    if (snapshotID != null || !sidColumn.isEmpty()) {
      if (simTimeColumn.size() - 1 > sidColumn.size()) {
        for (int i = simTimeColumn.size(); i > 1; i--) {
          sidColumn.add(null);
        }
      }
      sidColumn.add(snapshotID);
    }
  }

  /**
   * Init columns recording simulation and wall clock time as well as number of
   * iterations
   */
  public final void init() {
    assert wcStartTime == Long.MAX_VALUE;
    wcStartTime = System.currentTimeMillis();
  }

  /**
   * Check whether {@link #init()} has been called already
   *
   * @return false if {@link #init()} has been called
   */
  public final boolean isNotInitialized() {
    return wcStartTime == Long.MAX_VALUE;
  }

  private static final String IT_MS_REAL_COL_HEAD = "#It/ms (real)";

  private static final String ITERATIONS_COL_HEAD = "#Iterations";

  private static final String WALL_CLOCK_TIME_MS_COL_HEAD =
      "Elapsed wall clock time (ms)";

  private static final String SIM_TIME_COL_HEAD = "Simulation time";

  private static final String SNAP_ID_COL_HEAD = "Snapshot time/ID";

  /**
   * Template for {@link org.jamesii.core.observe.IInfoMapProvider#getInfoMap()}
   *
   * @return Map containing the columns recording simulation & wall-clock time
   */
  public Map<String, Object> getFinalInfoMap() {
    final int numItems = 6; // approx ;-)
    Map<String, Object> rv = new ArrayMap<>(numItems);
    rv.put(SIM_TIME_COL_HEAD, simTimeColumn.get(simTimeColumn.size() - 1));
    long deltaWallClockTime = wcLastTime - wcStartTime;
    rv.put(WALL_CLOCK_TIME_MS_COL_HEAD, deltaWallClockTime);
    if (lastNumIt > 0) {
      rv.put(ITERATIONS_COL_HEAD, lastNumIt);
    }
    if (lastNumIt > 0) {
      rv.put(IT_MS_REAL_COL_HEAD, deltaWallClockTime == 0 ? "NaN"
          : 1.0 * lastNumIt / deltaWallClockTime);
    }
    if (numItByWcTime != null && !numItByWcTime.isEmpty()) {
      rv.put("Var(#It/ms)",
          Variance.variance(numItByWcTime.subList(1, numItByWcTime.size())));
    }
    if (!sidColumn.isEmpty()) {
      rv.put(SNAP_ID_COL_HEAD, sidColumn.get(sidColumn.size() - 1));
    }
    return rv;
  }

  public Map<String, List<?>> getAllInfoAsMap() {
    final int numItems = 6; // approx ;-)
    Map<String, List<?>> rv = new ArrayMap<>(numItems);
    rv.put(SIM_TIME_COL_HEAD, simTimeColumn);
    rv.put(WALL_CLOCK_TIME_MS_COL_HEAD, wcTimeColumn);
    if (numItColumn != null && !numItColumn.isEmpty()) {
      rv.put(ITERATIONS_COL_HEAD, numItColumn);
    }
    if (recordNumItByWcTime) {
      rv.put(IT_MS_REAL_COL_HEAD, numItByWcTime);
    }
    if (recordNumItBySimTime) { // CHECK: content different from
                                // getFinalInfoMap, which may be confusing in
                                // some situations
      rv.put("#It/sec (sim)", numItBySimTime);
    }
    if (!sidColumn.isEmpty()) {
      rv.put(SNAP_ID_COL_HEAD, sidColumn);
    }
    return rv;
  }

}
