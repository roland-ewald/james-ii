/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.logging.Level;

import org.jamesii.core.math.random.distributions.ExponentialDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.snapshot.AbstractSnapshotObserver;
import org.jamesii.core.processor.RunnableProcessor;
import org.jamesii.core.util.eventset.Entry;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.hierarchy.Hierarchies;
import org.jamesii.core.util.hierarchy.IHierarchy;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.subvols.Subvol;
import simulator.mlspace.event.IMLSpaceEvent;
import simulator.mlspace.event.ISpatialEntityEvent;
import simulator.mlspace.event.NSMEvent;
import simulator.mlspace.eventrecord.ContSpaceEventRecord;
import simulator.mlspace.eventrecord.IEventRecord;
import simulator.mlspace.util.MLSpaceLogger;
import simulator.mlspace.util.MLSpaceLogger.DebugLevel;

/**
 * AbstractMLSpaceProcessor contains the common elements of the simulators for
 * the specific parts of MLSpace (NSM/subvol part, continuous space/moving
 * compartments part) and hybrid approaches combining them.
 * 
 * @author Arne Bittig
 * @param <E>
 *          Event type
 * @param <R>
 *          Type of produced {@link IEventRecord event effect record}
 */
public abstract class AbstractMLSpaceProcessor<E extends IMLSpaceEvent<?>, R extends IEventRecord>
    extends RunnableProcessor<Double> {

  /** Serialization ID */
  private static final long serialVersionUID = 3108324712784660543L;

  /**
   * Wrapper for hint passed to
   * {@link org.jamesii.core.observe.IObservable#changed(Object)} that contains
   * both the effect (i.e. the hint originally used) and the time of the next
   * event (for correct snapshot-taking behaviour introduced later)
   * 
   * @author Arne Bittig
   * @date 01.03.2013
   * @param <H>
   */
  public static class TimeAndHintContainer<H> extends Timer {
    private H hint;

    /**
     * Get the hint (to be called in observer that expects hint to be of type
     * TimeAndHintContainer)
     * 
     * @return Contained hint
     */
    public H getHint() {
      return hint;
    }

    /**
     * Set the hint (to be called in observable class)
     * 
     * @param hint
     */
    public void setHint(H hint) {
      this.hint = hint;
    }

    @Override
    public String toString() {
      return "[hint=" + hint + ", nextEventTime=" + getTime() + "]";
    }
  }

  /** Event queue used */
  private final IEventQueue<E, Double> eventQueue;

  /** Model entity factory for new compartment creation / instantiation */
  private final ModelEntityFactory modEntFac;

  /** Random number generator */
  private final IRandom rand;

  /** exponential distribution for sampling of next event times */
  private final ExponentialDistribution expDist;

  private final Timer simTimer;

  private final TimeAndHintContainer<R> eventRecordAndNextTime =
      new TimeAndHintContainer<>();

  /** mostly for the record / debugging purposes */
  private int numOfStep = 0;

  private MLSpaceLogger logger =
      new MLSpaceLogger(EnumSet.of(DebugLevel.BASIC_START_AND_END_INFO,
          DebugLevel.APPLIED_RULE_INFO), this);

  /**
   * @param model
   *          Model to process/simulate
   * @param eventQueue
   *          Event queue to use
   * @param rand
   *          Random number generator to use
   * @param timer
   *          Container of simulation time
   */
  protected AbstractMLSpaceProcessor(IModel model,
      IEventQueue<E, Double> eventQueue, IRandom rand, Timer timer) {
    super(model);
    this.eventQueue = eventQueue;
    this.rand = rand;
    // only one reasonable implementation of exponential distribution exists
    this.expDist = new ExponentialDistribution(rand);
    this.modEntFac = ((IMLSpaceModel) model).getModelEntityFactory();
    this.simTimer = timer;
  }

  protected void setLogger(MLSpaceLogger logger) {
    if (getTime() > 0) {
      throw new IllegalStateException(
          "Logger cannot be exchanged after simulation started!");
    }
    this.logger = logger;
  }

  @Override
  protected void nextStep() {
    if (numOfStep == 0) {
      logger.checkAndLog(DebugLevel.BASIC_START_AND_END_INFO, Level.INFO,
          getStartLogMessage());
    }
    numOfStep++;

    try {
      Entry<E, Double> evqe = eventQueue.dequeue();

      Double nextTime =
          evqe == null ? Double.POSITIVE_INFINITY : evqe.getTime();
      eventRecordAndNextTime.setTime(nextTime);
      changed(eventRecordAndNextTime); // new behavior: before next event
      eventRecordAndNextTime.setTime(null); // better safe than sorry /
      // fail-fast

      simTimer.setTime(nextTime);
      if (!nextTime.isInfinite()) {
        E event = evqe.getEvent(); // null ruled out by if condition
        R effect = handleEvent(event);
        logProcessedEvent(event, effect);
        updateEventQueue(event, effect);
        eventRecordAndNextTime.setHint(effect);
      } else {
        logger.checkAndLog(DebugLevel.BASIC_START_AND_END_INFO, Level.INFO,
            "Simulation ended at infinite time (which probably means"
                + " there were no more events to process)");
        stop();
      }
    } catch (Throwable t) {
      logger.log(Level.SEVERE, "Exception or error " + t);
      // logger adds sim time & # of step to log message (helpful if ex was
      // thrown in component that cannot access to this info)
      cleanUp();
      throw t;
    }
  }

  protected String getStartLogMessage() {
    return "Simulation started with " + eventQueue.size()
        + " events in queue.\nModel: " + getModel() + "\n"
        + getModel().getCompleteInfoString()
        + (rand == null ? ""
            : "\nRNG: " + rand.getClass().getSimpleName() + " with seed "
                + rand.getSeed()

    );
  }

  /**
   * Handle event (if possible; usually dispatch to appropriate separate method)
   * 
   * @param event
   *          IMLSpaceEvent<?> to handle
   * @return Effect of event (may be null if no handling possible)
   * @see #unknownEventWarning(IMLSpaceEvent) for not handleable cases
   */
  public abstract R handleEvent(E event);

  /**
   * Update the event queue entries for model components affected by the recent
   * event handling (see {@link #handleEvent(IMLSpaceEvent)})
   * 
   * @param event
   *          Recently handled event (null if not applicable)
   * @param effect
   *          Effect of recently handled event
   */
  public abstract void updateEventQueue(E event, R effect);

  /**
   * @return the logger
   */
  protected final MLSpaceLogger getLogger() {
    return logger;
  }

  @Override
  public Double getTime() {
    return simTimer.getTime();
  }

  /**
   * @return Model entity factory
   */
  protected ModelEntityFactory getModEntFac() {
    return modEntFac;
  }

  /**
   * @return Random number generator
   */
  protected IRandom getRand() {
    return rand;
  }

  /**
   * Delegate method for wrapping the event queue
   * 
   * @param event
   *          Event
   * @return Time of event
   * @see org.jamesii.core.util.eventset.IEventQueue#dequeue(java.lang.Object)
   */
  protected final Double dequeueEvent(E event) {
    return eventQueue.dequeue(event);
  }

  /**
   * Delegate method for wrapping the event queue
   * 
   * @param event
   *          Event
   * @param time
   *          Time of event
   * @see org.jamesii.core.util.eventset.IBasicEventQueue#enqueue(java.lang.Object,
   *      java.lang.Comparable)
   */
  protected final void enqueueEvent(E event, Double time) {
    if (time == null || time.isNaN()) {
      getLogger().log(Level.SEVERE, time + " enqueue for " + event);
    }
    eventQueue.enqueue(event, time);
  }

  /**
   * Delegate method for wrapping the event queue
   * 
   * @param event
   *          Event
   * @param newTime
   *          new time of Event
   * @see org.jamesii.core.util.eventset.IEventQueue#requeue(java.lang.Object,
   *      java.lang.Comparable)
   */
  protected final void requeueEvent(E event, double newTime) {
    if (Double.isNaN(newTime)) {
      getLogger().log(Level.SEVERE, "NaN requeue for " + event);
    }
    eventQueue.requeue(event, newTime);
  }

  protected final String getEventQueueType() {
    return eventQueue.getClass().getSimpleName();
  }

  /**
   * draw time of next stochastic simulation event from exponential distribution
   * 
   * @param rateSum
   * @return time until next event in relevant structure
   */
  protected final double getExpDistTimeToNextEvent(Double rateSum) {
    // if (rateSum == 0.0)
    // return Double.POSITIVE_INFINITY;
    return expDist.getRandomNumber(1.0 / rateSum);
  }

  /**
   * Display a warning that an event cannot be handled. (This method being
   * called would usually point to an implementation error.)
   * 
   * @param event
   *          Event
   */
  protected final void unknownEventWarning(IMLSpaceEvent<?> event) {
    logger.log(Level.SEVERE, this.getClassName() + " cannot handle event"
        + event + ". Undefined behaviour or NPE may result.");
  }

  /**
   * Log handled event and effect if specified by the debug level settings (see
   * {@link DebugLevel} and {@link #setDebugLevel(DebugLevel, Boolean)})
   * 
   * @param imlSpaceEvent
   *          Event just processed
   * @param effect
   */
  private void logProcessedEvent(IMLSpaceEvent<?> event, R effect) {
    boolean logEvent = event instanceof NSMEvent
        && logger.isDebugLevelSet(DebugLevel.PROCESSED_SV_EVENT_INFO)
        || event instanceof ISpatialEntityEvent
            && logger.isDebugLevelSet(DebugLevel.PROCESSED_COMP_EVENT_INFO);
    if (logEvent) {
      logger.log(Level.INFO, event + ": " + effect);
      return;
    }
    if (logger.isDebugLevelSet(DebugLevel.APPLIED_RULE_INFO)) {
      Collection<? extends MLSpaceRule> rules =
          effect instanceof ContSpaceEventRecord
              ? ((ContSpaceEventRecord) effect).getAllRules()
              : effect.getRules();
      if (!rules.isEmpty()) {
        logger.log(Level.INFO,
            event + ": Applied " + effect.getRules() + " as part of " + effect);
      }
    }
  }

  @Override
  public void cleanUp() {
    changed(AbstractSnapshotObserver.END_HINT); // notify observers one last
                                                // time
    logger.checkAndLog(DebugLevel.BASIC_START_AND_END_INFO, Level.INFO,
        "CleanUp called. Notified observers of simulation end.");
    super.cleanUp();
  }

  /**
   * @return Number of iteration / simulation step
   */
  public int getNumOfStep() {
    return numOfStep;
  }

  /**
   * helper method for observers (not every subclass needs to have an
   * appropriate field)
   * 
   * @return Compartments in the current system state
   */
  // default implementation of method to be overridden
  public IHierarchy<SpatialEntity> getSpatialEntities() {
    return Hierarchies.emptyHierarchy();
  }

  /**
   * helper method for observers (not every subclass needs to have an
   * appropriate field)
   * 
   * @return Subvols in the current system state
   */
  // default implementation of method to be overridden
  public Collection<Subvol> getSubvols() {
    return Collections.emptyList();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(getClass().getSimpleName());
    sb.append('@');
    sb.append(Integer.toHexString(hashCode()));
    if (getNumOfStep() > 0 || getTime() == 0) {
      // time > 0 && step == 0 possible for proc used inside hybrid proc
      sb.append(" at step " + getNumOfStep() + "@" + getTime());
    }
    return sb.toString();
  }
}