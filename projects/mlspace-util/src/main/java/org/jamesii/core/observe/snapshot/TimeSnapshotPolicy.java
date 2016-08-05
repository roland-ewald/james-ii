/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.snapshot;

import java.util.Iterator;

import org.jamesii.core.util.ITime;

/**
 * Snapshot policy for taking one snapshot or several in a row at given time
 * points. The time is taken from the {@link ITime}-implementing entity passed
 * to the constructor (e.g. a simulator for simulation time dependence, a
 * wrapper for system time for wall-clock dependent snapshots, ...).
 *
 * @author Arne Bittig
 * @param <T>
 *          Type of time base of entity on which snapshot-taking depends
 */
public class TimeSnapshotPolicy<T extends Comparable<T>> implements
    java.io.Serializable, ISnapshotPolicy<T> {

  private static final long serialVersionUID = 1795949015686244584L;

  /** Simulation time _after_ which to take snapshot */
  private final Iterator<? extends T> snapshotIterator;

  /** Time when to make next snapshot, null == none/infinity */
  private T timeOfNextSnapshot;

  private final ITime<T> timeProvider;

  /**
   * Create observer that takes snapshots at given times or in given intervals
   * (snapshotIterator or snapshotSpacing must be given; i.e. if the first is
   * null, the second must be > 0; if the first is not null, the second is
   * ignored)
   * 
   * @param entity
   *          Entity on which snapshot-taking depends, providing time
   * @param snapshotTimes
   *          Time points after which snapshots will be taken
   */
  public TimeSnapshotPolicy(ITime<T> entity, Iterable<? extends T> snapshotTimes) {
    this.timeProvider = entity;
    this.snapshotIterator = snapshotTimes.iterator();
    this.timeOfNextSnapshot =
        this.snapshotIterator.hasNext() ? this.snapshotIterator.next() : null;
  }

  /**
   * {@inheritDoc}
   * 
   * @return index, i.e. number of record to be taken (snapshot index * (number
   *         of followups - 1) + number of current followup)
   */
  @Override
  public T takeSnapshot() {
    if (timeOfNextSnapshot == null
    // if snapshot-taking is stopped after enoughSnapshots() returned
    // true for the first time, the former should never happen
        || getCurrentTime().compareTo(timeOfNextSnapshot) < 0) {
      return null;
    }
    T timeOfThisSnapshot = timeOfNextSnapshot;
    timeOfNextSnapshot =
        snapshotIterator.hasNext() ? snapshotIterator.next() : null;
    return timeOfThisSnapshot;
  }

  protected T getCurrentTime() {
    return timeProvider.getTime();
  }

  @Override
  public T takeSnapshot(Object hint) {
    return takeSnapshot();
  }

  /**
   * Determine whether enough snapshots have been taken
   * 
   * @return True if end of specified snapshot times has been reached
   */
  @Override
  public boolean enoughSnapshots() {
    return timeOfNextSnapshot == null;
  }
}
