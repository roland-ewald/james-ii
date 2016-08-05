/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.snapshot;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.util.ITime;

/**
 * Base class for observers taking snapshots. The {@link #update(IObservable)}
 * (or {@link #update(IObservable, Object)}) method consist of determining
 * whether at the current state of the observable, a snapshot shall be taken
 * (via {@link ISnapshotPolicy#takeSnapshot()} or
 * {@link ISnapshotPolicy#takeSnapshot(Object)}), and if so, delegates the
 * update work to {@link #updateState(IObservable, Object)} (or
 * {@link #updateState(IObservable, Object, Object)}). <br>
 *
 * With the appropriate {@link ISnapshotPolicy} and an observable that calls
 * {@link IObservable#changed(Object)} with, e.g., a hint containing the time of
 * the next update, but before the update is applied, a subclass of this one can
 * be used to take snapshots at exact time points that lie between simulation
 * events. For example, if the observable's {@link ITime#getTime() getTime}
 * method returns 9.987, the passed hint's {@link ITime#getTime() getTime}
 * method returns 10.001 and a snapshot is to be taken every whole unit of time,
 * the subclass' updateState method will be called when the observable is still
 * in the state it is/will be at time 10. The final parameter of updateState
 * will identify the snapshot (i.e. in this example it will be 10 such that the
 * state can be associated with that value instead of 9.987 (or, wrongly,
 * 10.001)). <br>
 *
 * A subclass' {@link #init()} method will always be called on the first update,
 * even when no snapshot is to be taken. {@link #cleanUp()} will be called if
 * the snapshot policy signals that {@link ISnapshotPolicy#enoughSnapshots()
 * enough snapshots} are taken or if {@link #update(IObservable, Object)} is
 * called with {@link #END_HINT} as hint. <br>
 *
 * If another, more complex implementation of {@link IObserver} shall be
 * extended (e.g. a notifying observer), one may as well use a snapshot policy
 * directly.
 *
 * @author Arne Bittig
 * @param <O>
 *          Observed entity type
 * @param <SID>
 *          ID type of snapshot policy
 * @date 14.02.2013
 */
public abstract class AbstractSnapshotObserver<O extends IObservable, SID>
implements IObserver<O> {

  /**
   * Dummy object to pass to {@link #update(IObservable, Object)} to signal
   * (potentially unexpected) calculation end
   */
  public static final Object END_HINT = new Object();

  /** Snapshot handler (determines when to take one and index of record) */
  private final ISnapshotPolicy<SID> snapshotPolicy;

  /** Number of calls to both update(...) methods (combined) */
  private long numOfUpdateCalls = 0;

  /**
   * @param snapshotPolicy
   *          Specification of when to take snapshots
   */
  protected AbstractSnapshotObserver(ISnapshotPolicy<SID> snapshotPolicy) {
    this.snapshotPolicy = snapshotPolicy;
  }

  @Override
  public final void update(O entity) {
    if (numOfUpdateCalls == 0) {
      init();
    }
    numOfUpdateCalls++;
    SID sid = snapshotPolicy.takeSnapshot();
    if (sid != null) {
      updateState(entity, sid);
      if (snapshotPolicy.enoughSnapshots()) {
        cleanUp();
      }
    }
  }

  @Override
  public final void update(O entity, Object hint) {
    if (numOfUpdateCalls == 0) {
      init();
    }
    numOfUpdateCalls++;
    if (END_HINT.equals(hint)) {
      updateState(entity, hint, null);
      cleanUp();
      return;
    }
    SID sid = snapshotPolicy.takeSnapshot(hint);
    if (sid != null) {
      updateState(entity, hint, sid);
      if (snapshotPolicy.enoughSnapshots()) {
        cleanUp();
      }
    }
  }

  /**
   * Get number of calls to both {@link #update(IObservable)} and
   * {@link #update(IObservable, Object)}. Will be 0 when {@link #init()} is
   * called, but at least 1 when {@link #updateState(IObservable, Object)} or
   * {@link #updateState(IObservable, Object, Object)} are called, even when
   * these are called directly after init.
   *
   * @return Number of calls to the update methods
   */
  public final long getNumberOfUpdateCalls() {
    return numOfUpdateCalls;
  }

  /**
   * Initialize. Called with first call to {@link #update(IObservable)} or
   * {@link #update(IObservable, Object)} no matter whether a snapshot needs to
   * be taken at the given state of the observable (
   * {@link #updateState(IObservable, Object)} or
   * {@link #updateState(IObservable,Object)} are still called directly
   * afterwards if the snapshot policy implies so).
   */
  protected abstract void init();

  /**
   * Do actual observation. Note that if the call does not follow
   * {@link #init()}, several preceding changes to the observed entity's state
   * were probably skipped (i.e. did not result in a call to this method).
   *
   * @param entity
   *          Observed entity
   * @param snapshotID
   *          Identifier for current snapshot to be taken
   */
  protected abstract void updateState(O entity, SID snapshotID);

  /**
   * Do actual observation, considering given hint. Note that if the call does
   * not follow {@link #init()}, several preceding changes to the observed
   * entity's state were probably skipped (i.e. did not result in a call to this
   * method).
   *
   * @param entity
   *          Observed entity
   * @param hint
   *          Hint that was passed
   * @param snapshotID
   *          Identifier for current snapshot to be taken
   */
  protected abstract void updateState(O entity, Object hint, SID snapshotID);

  /**
   * Clean up when no more snapshots shall be taken. This may be because the
   * {@link #END_HINT} was passed to {@link #update(IObservable, Object)} (via
   * {@link IObservable#changed(Object)}) or because
   * {@link ISnapshotPolicy#enoughSnapshots()} returned true right after a
   * snapshot.
   *
   * {@link #updateState(IObservable, Object)} or
   * {@link #updateState(IObservable, Object, Object)} will have been called
   * directly before this method (with null as snapshot ID in case of the
   * {@link #END_HINT} causing the call).
   *
   */
  protected abstract void cleanUp();
}
