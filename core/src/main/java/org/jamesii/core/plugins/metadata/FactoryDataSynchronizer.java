/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;

/**
 * Class that provides synchronisation methods for an
 * {@link IFactoryRuntimeDataStorage} and a list of registered {@link Factory}
 * instances.
 * 
 * @author Roland Ewald
 */
public final class FactoryDataSynchronizer {

  /**
   * Should not be instantiated.
   */
  private FactoryDataSynchronizer() {
  }

  /**
   * Synchronises the given data storage and the collection of registered
   * factories.
   * 
   * @param registeredFactories
   *          the factories that are registered
   * @param dataStorage
   *          the data storage
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void synchroniseFactories(
      Collection<Factory<?>> registeredFactories,
      IFactoryRuntimeDataStorage dataStorage) throws IOException {
    removeOldFactories(registeredFactories, dataStorage);
    addNewFactories(registeredFactories, dataStorage);
  }

  /**
   * Synchronises the given data storage and the collection of registered
   * abstract factories.
   * 
   * @param registeredFactories
   *          the abstract factories that are registered
   * @param dataStorage
   *          the data storage
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void synchroniseAbstractFactories(
      Set<Class<? extends AbstractFactory<?>>> registeredFactories,
      IFactoryRuntimeDataStorage dataStorage) throws IOException {
    removeOldAbstractFactories(registeredFactories, dataStorage);
    addNewAbstractFactories(registeredFactories, dataStorage);
  }

  /**
   * Removes the old factories.
   * 
   * @param registeredFactories
   *          the registered factories
   * @param dataStorage
   *          the data storage
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void removeOldFactories(
      Collection<Factory<?>> registeredFactories,
      IFactoryRuntimeDataStorage dataStorage) throws IOException {

    Set<Class<? extends Factory<?>>> registeredFactoryClasses = new HashSet<>();
    for (Factory<?> registeredFactory : registeredFactories) {
      registeredFactoryClasses
          .add((Class<? extends Factory<?>>) registeredFactory.getClass());
    }

    Set<Class<? extends Factory<?>>> factoriesToBeRemoved = new HashSet<>();
    for (Class<? extends Factory<?>> storedFactoryClass : dataStorage
        .getFactories()) {
      if (!registeredFactoryClasses.contains(storedFactoryClass)) {
        factoriesToBeRemoved.add(storedFactoryClass);
      }
    }

    for (Class<? extends Factory<?>> factoryToBeRemoved : factoriesToBeRemoved) {
      dataStorage.removeFactoryRuntimeData(factoryToBeRemoved);
    }

    dataStorage.flush();
  }

  /**
   * Removes the old abstract factories.
   * 
   * @param registeredFactories
   *          the registered factories
   * @param dataStorage
   *          the data storage
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void removeOldAbstractFactories(
      Set<Class<? extends AbstractFactory<?>>> registeredFactories,
      IFactoryRuntimeDataStorage dataStorage) throws IOException {

    Set<Class<? extends AbstractFactory<?>>> registeredFactoryClasses =
        new HashSet<>();
    for (Class<? extends AbstractFactory<?>> registeredAbstrFactory : registeredFactories) {
      registeredFactoryClasses.add(registeredAbstrFactory);
    }

    Set<Class<? extends AbstractFactory<?>>> abstrFactoriesToBeRemoved =
        new HashSet<>();
    for (Class<? extends AbstractFactory<?>> storedFactoryClass : dataStorage
        .getAbstractFactories()) {
      if (!registeredFactoryClasses.contains(storedFactoryClass)) {
        abstrFactoriesToBeRemoved.add(storedFactoryClass);
      }
    }

    for (Class<? extends AbstractFactory<?>> absFactoryToBeRemoved : abstrFactoriesToBeRemoved) {
      dataStorage.removeAbstractFactoryRuntimeData(absFactoryToBeRemoved);
    }

    dataStorage.flush();
  }

  /**
   * Adds new factories.
   * 
   * @param registeredFactories
   *          the registered factories
   * @param dataStorage
   *          the data storage
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void addNewFactories(
      Collection<Factory<?>> registeredFactories,
      IFactoryRuntimeDataStorage dataStorage) throws IOException {
    for (Factory<?> registeredFactory : registeredFactories) {
      if (dataStorage.getFactoryData(registeredFactory.getClass()) == null) {
        dataStorage.addFactoryRuntimeData(createRuntimeData(registeredFactory));
      }
    }
    dataStorage.flush();
  }

  /**
   * Creates the runtime data for a factory.
   * 
   * @param registeredFactory
   *          the registered factory
   */
  @SuppressWarnings("unchecked")
  // Ensured by getClass()
  private static <F extends Factory<?>> FactoryRuntimeData<F> createRuntimeData(
      F registeredFactory) {
    return new FactoryRuntimeData<>((Class<F>) registeredFactory.getClass());
  }

  /**
   * Creates the runtime data for an abstract factory.
   * 
   * @param registeredFactory
   *          the registered abstract factory
   * 
   * @return the abstract factory runtime data
   */
  @SuppressWarnings("unchecked")
  // Ensured by getClass()
  private static <F extends Factory<?>, AF extends AbstractFactory<F>> AbstractFactoryRuntimeData<F, AF> createRuntimeData(
      Class<AF> registeredFactory) {
    return new AbstractFactoryRuntimeData<>(registeredFactory,
        (Class<F>) SimSystem.getRegistry().getBaseFactoryForAbstractFactory(
            registeredFactory));
  }

  /**
   * Adds the new abstract factories.
   * 
   * @param registeredAbstractFactories
   *          the registered abstract factories
   * @param dataStorage
   *          the data storage
   */
  public static void addNewAbstractFactories(
      Set<Class<? extends AbstractFactory<?>>> registeredAbstractFactories,
      IFactoryRuntimeDataStorage dataStorage) {
    for (Class<? extends AbstractFactory<?>> registeredFactory : registeredAbstractFactories) {
      processRegisteredFactory(dataStorage, registeredFactory);
    }
  }

  @SuppressWarnings("unchecked")
  // Abstract factories belong to a certain concrete factory
  private static <F extends Factory<?>> void processRegisteredFactory(
      IFactoryRuntimeDataStorage dataStorage,
      Class<? extends AbstractFactory<?>> registeredFactory) {
    if (dataStorage
        .getAbstractFactoryData((Class<? extends AbstractFactory<F>>) registeredFactory) == null) {
      dataStorage
          .addAbstractFactoryRuntimeData(createRuntimeData((Class<? extends AbstractFactory<F>>) registeredFactory));
    }
  }

}
