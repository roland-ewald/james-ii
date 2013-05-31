/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.listener;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.observe.INotifyingObserver;
import org.jamesii.core.observe.IObservable;

/**
 * This is describes the functions of a listener for an observer. The listener
 * will not directly interact with the entity, but it receives a reference to
 * the observer when an update occurred.
 * 
 * @author Roland Ewald
 */
public interface IObserverListener {

  /**
   * Signals that a notifying observer has been associated with this observer
   * listener, should be used for initialisation. The new run is identified by
   * the runtime information that is passed. This also can be used for
   * re-initialising the simulation run controls for the user if a new
   * replication occurring.
   * 
   * @param srti
   *          the new simulation runtime information
   */
  void init(ComputationTaskRuntimeInformation srti);

  /**
   * Notifies observer listener that the observer was updated.
   * 
   * @param observer
   *          the updated observer
   */
  void updateOccurred(INotifyingObserver<? extends IObservable> observer);
}
