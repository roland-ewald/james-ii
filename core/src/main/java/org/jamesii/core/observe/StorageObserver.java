/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import org.jamesii.core.data.storage.IDataStorage;

/**
 * Simple implementation of an abstract superclass for observers that use a data
 * storage.
 * 
 * @author Roland Ewald
 * @param <E>
 *          Type of observed entities
 * @param <D>
 *          Type of used data storage
 * @date Jun 05, 2007
 */
public abstract class StorageObserver<E extends IObservable, D extends IDataStorage>
    implements IStoringObserver<E, D> {

  /**
   * Reference to data storage that shall be used.
   */
  private D dataStorage = null;

  @Override
  public final D getDataStorage() {
    return dataStorage;
  }

  @Override
  public void setDataStorage(D dataStorage) {
    this.dataStorage = dataStorage;
  }

  @Override
  public void update(E entity, Object hint) {
    update(entity);
  }

}
