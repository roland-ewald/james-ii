/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.snapshot;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.ITime;

/**
 * Snapshot policy that tries to extract time from the hint of
 * {@link #takeSnapshot(Object)} and, if successful, uses this one instead of
 * the time of the entity passed to the constructor. If no fallback
 * time-providing entity is given and a hint cannot be interpreted, an
 * {@link IllegalStateException} will be thrown.
 *
 * @author Arne Bittig
 * @param <T>
 *          Time base type
 * @date 28.02.2013
 */
public class TimeHintSnapshotPolicy<T extends Comparable<T>> extends
    TimeSnapshotPolicy<T> {

  // /**
  // * Specification for how to handle uninterpretable hints if no fall-back
  // * time-providing entity is present
  // *
  // * @author Arne Bittig
  // * @date 28.02.2013
  // */
  // public static enum WrongHintHandling {
  // /** Ignore call and no not take snapshot */
  // NO_SNAPSHOT,
  // // /** Take snapshot */ // not easily implemented
  // // SNAPSHOT,
  // /** Throw IllegalStateException */
  // EXCEPTION
  // }

  private static final long serialVersionUID = 8827848714507650476L;

  /**
   * entity providing time if no or unappropriate hint, or null if "hint only"
   * (exception otherwise)
   */
  private ITime<T> timeProvider;

  private T currentTime = null;

  // private WrongHintHandling whh;

  /**
   * Snapshot policy considering only the time passed as hint
   * 
   * @param snapshotTimes
   *          Time points after which snapshots will be taken
   */
  // * @param whh
  // * Specifier for how to handle hints without time
  public TimeHintSnapshotPolicy( // WrongHintHandling whh,
      Iterable<? extends T> snapshotTimes) {
    this(null, snapshotTimes); // , whh);
  }

  /**
   * Snapshot policy based on time hints and a time-providing entity as
   * fall-back
   * 
   * @param entity
   *          Entity providing time
   * @param snapshotTimes
   *          Time points after which snapshots will be taken
   */
  public TimeHintSnapshotPolicy(ITime<T> entity,
      Iterable<? extends T> snapshotTimes) {
    super(entity, snapshotTimes);
    this.timeProvider = entity;
    // this.whh = whh;
  }

  @Override
  protected T getCurrentTime() {
    if (currentTime != null) {
      return currentTime;
    }
    if (timeProvider != null) {
      return timeProvider.getTime();
    }
    throw new IllegalStateException("No hint with time "
        + "and no fallback time-providing entity");
  }

  @Override
  public T takeSnapshot(Object hint) {
    if (hint instanceof ITime<?>) {
      try {
        currentTime = ((ITime<T>) hint).getTime();
      } catch (ClassCastException ex) {
        SimSystem.report(Level.SEVERE,
            "Time of wrong time base type provided by " + hint);
        SimSystem.report(ex);
      }
    }
    T rv = super.takeSnapshot();
    currentTime = null;
    return rv;
  }

}
