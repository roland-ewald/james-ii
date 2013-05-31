/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata;

import java.io.IOException;
import java.util.Set;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;

/**
 * Defines the interface of all components that can provide the registry with
 * data storage. An adaptive registry requires to store additional data.
 * 
 * @author Roland Ewald
 */
public interface IFactoryRuntimeDataStorage {

  /**
   * Open the data storage.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  void open() throws IOException;

  /**
   * Checks whether data storage has already been opened.
   * 
   * @return true, if data storage has already been opened
   */
  boolean isOpen();

  /**
   * Close the data storage.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  void close() throws IOException;

  /**
   * Resets data storage, i.e. all data is deleted.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  void reset() throws IOException;

  /**
   * Flushes the data storage.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  void flush() throws IOException;

  /**
   * Gets factory data.
   * 
   * @return the factory data, null if non-existent
   */
  <F extends Factory<?>> FactoryRuntimeData<F> getFactoryData(
      Class<F> factoryClass);

  /**
   * Gets the factory iterator.
   * 
   * @return the factory iterator
   */
  Set<Class<? extends Factory<?>>> getFactories();

  /**
   * Adds the factory runtime data.
   * 
   * @param factoryRuntimeData
   *          the factory runtime data
   */
  void addFactoryRuntimeData(
      FactoryRuntimeData<? extends Factory<?>> factoryRuntimeData);

  /**
   * Removes the factory runtime data.
   * 
   * @param factoryRuntimeData
   *          the factory runtime data
   * 
   * @return the runtime data that was removed
   */
  FactoryRuntimeData<? extends Factory<?>> removeFactoryRuntimeData(
      Class<? extends Factory<?>> factoryRuntimeData);

  /**
   * Gets the abstract factory data.
   * 
   * @param abstractFactoryClass
   *          the abstract factory class
   * 
   * @return the abstract factory data
   */
  <F extends Factory<?>, AF extends AbstractFactory<F>> AbstractFactoryRuntimeData<F, AF> getAbstractFactoryData(
      Class<AF> abstractFactoryClass);

  /**
   * Gets the set of abstract factories.
   * 
   * @return the set of abstract factories
   */
  Set<Class<? extends AbstractFactory<?>>> getAbstractFactories();

  /**
   * Adds the abstract factory runtime data.
   * 
   * @param abstractFactoryRuntimeData
   *          the factory runtime data
   */
  void addAbstractFactoryRuntimeData(
      AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>> abstractFactoryRuntimeData);

  /**
   * Removes the abstract factory runtime data.
   * 
   * @param abstractFactoryRuntimeData
   *          the abstract factory runtime data
   * 
   * @return the abstract factory runtime data that was removed
   */
  AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>> removeAbstractFactoryRuntimeData(
      Class<? extends AbstractFactory<?>> abstractFactoryRuntimeData);

  /**
   * Sets the failure tolerance and stores the setting.
   * 
   * @param failureTolerance
   *          the new failure tolerance
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  void setAndStoreFailureTolerance(FailureTolerance failureTolerance)
      throws IOException;

  /**
   * Gets the failure tolerance.
   * 
   * @return the failure tolerance
   */
  FailureTolerance getFailureTolerance();

}
