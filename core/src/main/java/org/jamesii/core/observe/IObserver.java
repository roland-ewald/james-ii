/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

/**
 * Interface to be implemented by all observers.
 * 
 * @author Roland Ewald
 * 
 *         Date: 03.05.2007
 * @param <E>
 *          Type of entity to observe
 */
public interface IObserver<E extends IObservable> {

  /**
   * This function is called when an entity changes its state.
   * 
   * @param entity
   *          the entity that was updated.
   */
  void update(E entity);

  /**
   * This function is called when an entity was updated and a hint can be
   * provided as to what part of the object's state has been changed.
   * 
   * @param entity
   *          the entity
   * @param hint
   *          the hint
   */
  void update(E entity, Object hint);

}
