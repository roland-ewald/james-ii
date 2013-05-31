/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import org.jamesii.core.observe.listener.IObserverListener;

/**
 * This is the interface for all observers that are able to manage a set of
 * listeners. Listeners are of type {@link IObserverListener}. One could also
 * observer observers, but this would lead to *very* unclear (and potentially
 * cyclic!) code.
 * 
 * @author Roland Ewald
 * @param <E>
 *          Type of entity to observe
 */
public interface INotifyingObserver<E extends IObservable> extends IObserver<E> {

  /**
   * Adds a listener to be notified upon an update.
   * 
   * @param listener
   *          listener to be added
   */
  void addListener(IObserverListener listener);

  /**
   * Removes a listener.
   * 
   * @param listener
   *          listener to be removed
   */
  void removeListener(IObserverListener listener);

}
