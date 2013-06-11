/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

/**
 * The interface IObservable has to be implemented by all classes which shall be
 * observable.
 * 
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IObservable {

  /**
   * Gets the mediator.
   * 
   * @return the mediator
   */
  IMediator getMediator();

  /**
   * Register the observer passed as parameter at the mediator. If no mediator
   * is set an exception will be raised.
   * 
   * @param observer
   *          The observer to be registered.
   */
  void registerObserver(IObserver observer);

  /**
   * Set a mediator. All observers will be registered at this mediator.
   * 
   * @param mediator
   */
  void setMediator(IMediator mediator);

  /**
   * Unregister the given observer.
   * 
   * @param observer
   */
  void unregisterObserver(IObserver observer);

  /**
   * Unregister all observers at once.
   */
  void unregisterObservers();

  /**
   * Called when the entity has been changed (for observer notification).
   */
  void changed();

  /**
   * Called when the entity has been changed (for observer notification).
   * 
   * @param hint
   *          to be passed to the observer, e.g., what has changed, or what
   *          induced the change
   */
  void changed(Object hint);

}
