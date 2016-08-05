/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.snapshot;

/**
 * A snapshot policy defines when snapshots (most often of a simulation run)
 * should be taken (most likely by an observer). This may depend on time (sim or
 * wall clock), number of simulation steps or some other simulation properties.
 *
 * Implementations will usually keep a reference to the observed entity if the
 * taking of snapshots depends on its state (e.g. the simulation time). The
 * central methods shall return null when no snapshot is to be taken and
 * otherwise an identifier for this snapshot. For example, when a simulation
 * shall be observed at certain time points, this could be the time of desired
 * observation, which may not exactly match the exact time of a simulation step.
 *
 * @author Arne Bittig
 * @param <SID>
 *          Snapshot ID type
 * @date 05.02.2013
 */
public interface ISnapshotPolicy<SID> {

  /**
   * Check whether snapshot should be taken at given simulation step and/or
   * time, get ID
   * 
   * @return true if snapshot shall be taken given the current state of observed
   */
  SID takeSnapshot();

  /**
   * Check whether snapshot should be taken at given simulation step and/or
   * time, considering given hint. Semi-optional operation: may ignore argument,
   * but then should do the same as a call to {@link #takeSnapshot()}.
   * 
   * @param hint
   *          Hint on whether to take snapshots.
   * @return true if snapshot shall be taken given the current state of observed
   */
  SID takeSnapshot(Object hint);

  /**
   * Determine whether enough snapshots have been taken
   * 
   * @return True iff {@link #takeSnapshot()} cannot possibly return true in any
   *         subsequent call
   */
  boolean enoughSnapshots();
}