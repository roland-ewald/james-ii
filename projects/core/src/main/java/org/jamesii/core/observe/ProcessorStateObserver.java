/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import org.jamesii.core.processor.ProcessorState;

/**
 * An observer for a processor state, to be updated on every simulation step.
 * 
 * $Date$.
 * 
 * $Rev$.
 * 
 * 
 * 
 * @param <E>
 * 
 */
public abstract class ProcessorStateObserver<E extends ProcessorState> extends
    NotifyingObserver<E> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -1601578123842075130L;

  /**
   * Determines time span between notifying on updates (in simulation time).
   */
  private double cycle;

  /**
   * Current drawing time. If simulation time is larger than that, all listeners
   * get notified.
   */
  private double notificationTime = 0;

  /**
   * Simulation time.
   */
  private double simTime = 0;

  /**
   * Initializes observer for immediate notification.
   */
  public ProcessorStateObserver() {
    this(0);
  }

  /**
   * Default constructor.
   * 
   * @param cycle
   *          notification cycle time
   */
  public ProcessorStateObserver(double cycle) {
    this.cycle = cycle;
  }

  /**
   * This method returns the time of the processor which is automatically
   * updated each time this observer gets activated. However, it will only be
   * correct if the observer gets updated at least once per time step of the
   * simulation algorithm.
   * 
   * @return the sim time
   */
  public double getSimTime() {
    return simTime;
  }

  /**
   * Get simulation time. Depends on processor implementation.
   * 
   * @param state
   *          the processor state
   * @return simulation time
   */
  protected abstract double getSimTime(E state);

  @Override
  // TODO: is this working?
  public void handleUpdate(E entity) {
    simTime = getSimTime(entity);

    if (cycle == 0) {
      notifyListeners();
    } else {
      if (simTime >= notificationTime) {
        notifyListeners();
      }
      while (simTime > notificationTime) {
        notificationTime += cycle;
      }
    }

  }

}
