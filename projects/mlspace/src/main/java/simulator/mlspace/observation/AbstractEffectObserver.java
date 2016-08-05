/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation;

import java.util.logging.Level;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.snapshot.AbstractSnapshotObserver;
import org.jamesii.core.util.logging.ApplicationLogger;

import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.AbstractMLSpaceProcessor.TimeAndHintContainer;
import simulator.mlspace.eventrecord.IEventRecord;

/**
 * ML-Space observer writing core content of the last event (passed to
 * {@link #update(org.jamesii.core.observe.IObservable, Object)} as
 * {@link IEventRecord} hint) to file
 *
 * @author Arne Bittig
 * @param <R>
 *          Expected subclass of {@link IEventRecord}
 * @date Apr 11, 2012
 */
public abstract class AbstractEffectObserver<R extends IEventRecord>
    implements IObserver<AbstractMLSpaceProcessor<?, R>> {

  private int updateWrongHintCalled = 0;

  private boolean initSuccess = false;

  @Override
  public final void update(AbstractMLSpaceProcessor<?, R> proc) {
    if (!initSuccess) {
      initSuccess = init(proc);
    }
    logWrongHint(proc.isStopping(), proc, null);
  }

  private void logWrongHint(boolean finished, IEntity entity, Object hint) {
    if (!finished && updateWrongHintCalled++ > 0) {
      ApplicationLogger.log(Level.WARNING,
          "Effect observer must be called with hint"
              + (hint == null ? "!" : " of type IEventRecord, not " + hint)
              + " Entity: " + entity);
    }
  }

  private boolean unsuitable = false; // warn only once about incorrect hint

  @SuppressWarnings("unchecked")
  @Override
  public final void update(AbstractMLSpaceProcessor<?, R> proc, Object hint) {
    if (!initSuccess) {
      initSuccess = init(proc);
    }
    boolean isFinished =
        AbstractSnapshotObserver.END_HINT.equals(hint) || proc.isStopping();
    if (isFinished) {
      cleanUp(proc);
    }
    Object hint2 = hint instanceof TimeAndHintContainer<?>
        ? ((TimeAndHintContainer<?>) hint).getHint() : hint;
    if (hint2 instanceof IEventRecord) {
      try {
        recordEffect(proc.getTime(), (R) hint2);
      } catch (ClassCastException ex) {
        if (!unsuitable) {
          ApplicationLogger.log(Level.SEVERE,
              this.getClass().getName() + " not suitable for simulation"
                  + " that produces hints of type "
                  + hint2.getClass().getSimpleName());
          ApplicationLogger.log(Level.WARNING, "", ex);
        }
        unsuitable = true;
      }
    } else {
      logWrongHint(isFinished, proc, hint2);

    }

  }

  /**
   * Init the observation process & data -- will be called at the first update
   * call with the given entity, which might not be the one of interest. Thus,
   * it will be called again and again at every update call until it returns
   * true.
   * 
   * @param proc
   *          Processor entity with which
   *          {@link #update(AbstractMLSpaceProcessor)} or
   *          {@link #update(AbstractMLSpaceProcessor, Object)} was called
   * @return success value
   */
  protected abstract boolean init(AbstractMLSpaceProcessor<?, R> proc);

  /**
   * @param time
   *          Simulation time
   * @param effect
   *          Effect to record
   */
  protected abstract void recordEffect(Double time, R effect);

  /**
   * Clean up after simulation is finished, e.g. write files
   * 
   * @param proc
   *          Simulator
   */
  protected abstract void cleanUp(AbstractMLSpaceProcessor<?, R> proc);

}