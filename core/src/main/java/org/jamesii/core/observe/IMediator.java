/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import java.util.List;

/**
 * The interface IMediator.
 * 
 * A mediator holds lists of <entity, observer> pairs. Whenever an entity is
 * modified the mediator is informed. A mediator can use different updating
 * schemas, e.g. direct update, cached update (but therefore the entity needs to
 * be "cloned", ... .
 * 
 * 
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public interface IMediator {

  /**
   * Register a pair of entity and observer at the mediator.
   * 
   * @param entity
   *          the entity which normally calls this method and wants to register
   *          an observer
   * @param observer
   *          which should be registered to the entity
   */
  void register(IObservable entity, IObserver<?> observer);

  /**
   * Remove the entity from the mediator.
   * 
   * @param entity
   *          the entity
   */
  void unRegister(IObservable entity);

  /**
   * Remove the given observer from the list of observers of the entity given.
   * 
   * @param entity
   *          the entity
   * @param observer
   *          the observer
   */
  void unRegister(IObservable entity, IObserver<?> observer);

  /**
   * Inform observers of the entity given that a change has occurred.
   * 
   * @param sender
   *          the sender
   */
  void notifyObservers(IObservable sender);

  /**
   * Inform observers of the entity given that a change has occurred, attach as
   * additional information the passed object.
   * 
   * @param sender
   *          the sender
   * @param arg
   *          the arg
   */
  void notifyObservers(IObservable sender, Object arg);

  /**
   * Return the list of entities registered for the given entity.
   * 
   * @param entity
   *          the entity
   * 
   * @return the observer
   */
  List<IObserver<?>> getObserver(IObservable entity);

}
