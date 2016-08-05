/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.snapshot;

import java.util.Calendar;
import java.util.Iterator;

import org.jamesii.core.util.ITime;

/**
 * {@link ISnapshotPolicy Snapshot policies} not warranting their own files
 *
 * @author Arne Bittig
 * @date 05.02.2013
 */
public final class SnapshotPolicies {

  private SnapshotPolicies() {
  }

  /**
   * Wall-clock time dependent snapshot policy
   *
   * @param intervalMS
   *          Interval in which to take snapshots (in milliseconds)
   * @return Snapshot policy
   */
  public static ISnapshotPolicy<Long> wallClock(final long intervalMS) {
    return new TimeSnapshotPolicy<>(new WallClockTime(), new LongStepIterator(
        intervalMS));
  }

  private static final class WallClockTime implements ITime<Long> {
    @Override
    public Long getTime() {
      return Calendar.getInstance().getTimeInMillis();
    }
  }

  /**
   * @author Arne Bittig
   * @date 28.02.2013
   */
  private static final class LongStepIterator implements Iterable<Long> {
    private final long intervalMS;

    /**
     * @param intervalMS
     */
    LongStepIterator(long intervalMS) {
      this.intervalMS = intervalMS;
    }

    @Override
    public Iterator<Long> iterator() {
      return new Iterator<Long>() {

        private long beforeLast = -intervalMS;

        @Override
        public boolean hasNext() {
          return true;
        }

        @Override
        public Long next() {
          beforeLast += intervalMS;
          return beforeLast;
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }
  }

  /**
   * Step count dependent snapshot policy. Counts the number of evaluations
   * internally, thus it must be used where the snapshot policy is evaluated
   * exactly once per step of the observed entity (and it is also a bad idea to
   * pass the same snapshot policy to multiple observers, as evaluation is not
   * free of side effects).
   *
   * @param callsToPass
   *          Step interval between snapshots
   * @return Snapshot policy based on step count
   */
  public static ISnapshotPolicy<Long> callCount(long callsToPass) {
    return new TimeSnapshotPolicy<>(new CallCountTime(), new LongStepIterator(
        callsToPass));
  }

  /**
   * Dummy {@link ITime} returning the (long) number of calls to
   * {@link #getTime()}
   *
   * @author Arne Bittig
   * @date 28.02.2013
   */
  private static final class CallCountTime implements ITime<Long> {
    private long counter = 0;

    @Override
    public Long getTime() {
      return ++counter;
    }
  }

  /**
   * Trivial snapshot policy: never take snapshots. Consider avoiding creation
   * of the relevant snapshot-taking entity in the first place.
   *
   * @return Trivial snapshot policy: never take snapshots
   */
  public static ISnapshotPolicy<Boolean> never() {
    return NEVER;
  }

  private static final ISnapshotPolicy<Boolean> NEVER = new Never();

  /**
   * Trivial policy of never taking snapshots.
   *
   * @author Arne Bittig
   * @date 14.02.2013
   */
  private static class Never implements ISnapshotPolicy<Boolean> {

    @Override
    public Boolean takeSnapshot() {
      return false;
    }

    @Override
    public Boolean takeSnapshot(Object hint) {
      return takeSnapshot();
    }

    @Override
    public boolean enoughSnapshots() {
      return true;
    }
  }

  /**
   * Trivial snapshot policy: always take snapshots.
   *
   * @return Trivial snapshot policy: never take snapshots
   */
  public static ISnapshotPolicy<Boolean> always() {
    return ALWAYS;
  }

  private static final ISnapshotPolicy<Boolean> ALWAYS = new Always();

  /**
   * Trivial policy of always taking snapshots.
   *
   * @author Arne Bittig
   * @date 14.02.2013
   */
  private static class Always implements ISnapshotPolicy<Boolean> {

    @Override
    public Boolean takeSnapshot() {
      return true;
    }

    @Override
    public Boolean takeSnapshot(Object hint) {
      return takeSnapshot();
    }

    @Override
    public boolean enoughSnapshots() {
      return false;
    }
  }

}
