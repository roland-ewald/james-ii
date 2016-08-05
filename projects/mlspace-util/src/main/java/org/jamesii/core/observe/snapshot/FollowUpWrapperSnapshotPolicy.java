/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.snapshot;

/**
 * Wrapper class delegating the snapshot-taking decision, but returning the same
 * value for {@link #takeSnapshot()} several times in a row (to allow taking a
 * snapshot of one state and the very next one, no matter the original snapshot
 * condition). The internal follow-up counter is reset when the condition of the
 * wrapped policy is fulfilled before the desired amount of follow-up snapshots
 * have been taken.
 *
 * @author Arne Bittig
 * @param <SID>
 *          Snapshot ID type
 * @date 14.02.2013
 */
public class FollowUpWrapperSnapshotPolicy<SID> implements ISnapshotPolicy<SID> {

  private final ISnapshotPolicy<SID> wrappedPolicy;

  private final int numFollowUps;

  private int pendingFollowUps;

  private SID lastNonNullSID = null;

  /**
   * @param wrappedPolicy
   * @param followUps
   */
  public FollowUpWrapperSnapshotPolicy(ISnapshotPolicy<SID> wrappedPolicy,
      int followUps) {
    super();
    this.wrappedPolicy = wrappedPolicy;
    this.numFollowUps = followUps;
    this.pendingFollowUps = followUps;
  }

  @Override
  public SID takeSnapshot() {
    return takeSnapshotOrFillowUp(wrappedPolicy.takeSnapshot());
  }

  @Override
  public SID takeSnapshot(Object hint) {
    return takeSnapshotOrFillowUp(wrappedPolicy.takeSnapshot(hint));
  }

  private SID takeSnapshotOrFillowUp(SID sid) {
    if (sid != null) {
      pendingFollowUps = numFollowUps;
      lastNonNullSID = sid;
      return sid;
    } else if (pendingFollowUps > 0) {
      pendingFollowUps--;
      return lastNonNullSID;
    }
    return null; // == sid
  }

  @Override
  public boolean enoughSnapshots() {
    return pendingFollowUps == numFollowUps && wrappedPolicy.enoughSnapshots();
  }

  /**
   * @return true iff {@link #takeSnapshot()} the last time
   *         {@link #takeSnapshot()} returned true the same method of the
   *         wrapped policy would have returned false
   */
  public boolean isFollowUp() {
    return pendingFollowUps != numFollowUps;
  }

}