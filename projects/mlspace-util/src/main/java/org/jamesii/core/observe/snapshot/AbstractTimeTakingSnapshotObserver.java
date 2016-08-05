/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.snapshot;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.util.ITime;

/**
 * * @author Arne Bittig
 * 
 * @param <O>
 *          Observed entity type
 * @param <SID>
 *          ID type of snapshot policy
 * @date 14.05.2014
 */
public abstract class AbstractTimeTakingSnapshotObserver<O extends IObservable & ITime<Double>, SID>
    extends AbstractSnapshotObserver<O, SID> {

  private final TimeRecorder<SID> timeRec;

  /** number of current snapshot */
  private int idxSnapshot = 0;

  protected AbstractTimeTakingSnapshotObserver(
      ISnapshotPolicy<SID> snapshotPolicy, TimeRecorder<SID> timeRecorder) {
    super(snapshotPolicy);
    this.timeRec = timeRecorder;
  }

  @Override
  protected void init() {
    if (timeRec != null && timeRec.isNotInitialized()) {
      timeRec.init();
    }
  }

  @Override
  protected void updateState(O proc, Object hint, SID snapshotID) {
    updateState(proc, snapshotID);
  }

  @Override
  protected void updateState(O proc, SID snapshotID) {
    if (timeRec != null) {
      timeRec.recordTime(proc.getTime(), getNumberOfUpdateCalls(), snapshotID);
    }
    updateState(proc, idxSnapshot++);

  }

  /**
   * Actual update method (with a different name so the common update mechanism
   * can be here in the {@link #update(org.jamesii.core.observe.IObservable)}
   * method without super.update having to be called in the subclass' overridden
   * method.)
   * 
   * @param proc
   *          Processor entity whose state to observe
   * @param idx
   *          Number of snapshot to be taken
   */
  protected abstract void updateState(O proc, int i);

  /**
   * @return time-recording instance (same as passed to the constructor, i.e.
   *         may be null)
   */
  protected final TimeRecorder<SID> getTimeRecord() {
    return timeRec;
  }

}
