/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import org.jamesii.core.data.storage.IDataStorage;

/**
 * Observer that records data in a given {@link IDataStorage DataStorage}.
 * 
 * @author Roland Ewald
 * @param <E>
 *          Type of observed entities
 * @param <D>
 *          Type of used data storage
 * @date Jun 05, 2007
 */
public interface IStoringObserver<E extends IObservable, D extends IDataStorage>
    extends IObserver<E> {

  /**
   * Get the data storage used by this observer (null, if no data storage is
   * used).
   * 
   * @return the data storage
   */
  D getDataStorage();

  /**
   * Sets the data storage to which the observer saves the data.
   * 
   * @param dataStorage
   *          the data storage
   */
  void setDataStorage(D dataStorage);

}
