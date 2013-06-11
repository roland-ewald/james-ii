/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata.file;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.plugins.metadata.AbstractFactoryRuntimeData;
import org.jamesii.core.plugins.metadata.FactoryRuntimeData;
import org.jamesii.core.plugins.metadata.FailureTolerance;
import org.jamesii.core.plugins.metadata.IFactoryRuntimeDataStorage;
import org.jamesii.core.util.misc.Files;

/**
 * Naive file-based implementation of a storage-solution for the registry.
 * 
 * @author Roland Ewald
 */
public class RegFileDStorage implements IFactoryRuntimeDataStorage {

  /** The registry file. */
  private final File regFile;

  /** Flag that determines whether the factory has already been opened. */
  private boolean opened = false;

  /** The factory data map: factory class => runtime data. */
  private Map<Class<? extends Factory<?>>, FactoryRuntimeData<? extends Factory<?>>> factoryData;

  /** The abstract factory data: abstract factory class => runtime data. */
  private Map<Class<? extends AbstractFactory<?>>, AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>>> abstractFactoryData;

  /** The failure tolerance of the registry. */
  private FailureTolerance failureTolerance;

  /**
   * Instantiates a new registry file data storage.
   * 
   * @param fileName
   *          the file name
   * @throws IOException
   *           if something goes wrong
   */
  public RegFileDStorage(String fileName) throws IOException {
    regFile = new File(fileName);
  }

  @Override
  public synchronized void open() throws IOException {

    // Create empty file on start-up
    if (!regFile.exists()) {
      reset();
    }

    RuntimeData rtd = null;
    try {
      rtd = (RuntimeData) Files.load(regFile.getAbsolutePath());
      factoryData = rtd.getFactoryData();
      abstractFactoryData = rtd.getAbstractFactoryData();
      failureTolerance = rtd.getFailureTolerance();
      checkForLoadProblems();
      opened = true;
    } catch (Throwable t) { // NOSONAR:robustness:no_kind_of_error_should_prevent_startup
      SimSystem.report(Level.SEVERE, "Factory data could not be loaded.", t);
      reset();
    } finally {
      opened = true;
    }
  }

  /**
   * Checks whether an important data structure is currently null (may happen
   * while loading malformed or incomplete registry storage file). In this case,
   * *all* variables are re-set.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void checkForLoadProblems() throws IOException {
    if (factoryData == null || abstractFactoryData == null
        || failureTolerance == null) {
      reset();
    }
  }

  @Override
  public void flush() throws IOException {
    save();
  }

  @Override
  public final void reset() throws IOException {
    factoryData = new HashMap<>();
    abstractFactoryData = new HashMap<>();
    failureTolerance = FailureTolerance.ACCEPT_UNTESTED;
    save();
  }

  /**
   * Save current state.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void save() throws IOException {
    Files.save(new RuntimeData(abstractFactoryData, factoryData,
        failureTolerance), regFile.getAbsolutePath());
  }

  @Override
  public void close() throws IOException {
    save();
  }

  @Override
  public Set<Class<? extends Factory<?>>> getFactories() {
    return Collections.unmodifiableSet(factoryData.keySet());
  }

  @Override
  @SuppressWarnings("unchecked")
  // Ensured by semantics of mapping
  public <F extends Factory<?>> FactoryRuntimeData<F> getFactoryData(
      Class<F> factoryClass) {
    return (FactoryRuntimeData<F>) factoryData.get(factoryClass);
  }

  @Override
  public void addFactoryRuntimeData(
      FactoryRuntimeData<? extends Factory<?>> factoryRuntimeData) {
    factoryData.put(factoryRuntimeData.getFactory(), factoryRuntimeData);
  }

  @Override
  public FactoryRuntimeData<? extends Factory<?>> removeFactoryRuntimeData(
      Class<? extends Factory<?>> factory) {
    return factoryData.remove(factory);
  }

  @Override
  @SuppressWarnings("unchecked")
  // Ensured by semantics of mapping
  public <F extends Factory<?>, AF extends AbstractFactory<F>> AbstractFactoryRuntimeData<F, AF> getAbstractFactoryData(
      Class<AF> abstractFactoryClass) {
    return (AbstractFactoryRuntimeData<F, AF>) abstractFactoryData
        .get(abstractFactoryClass);
  }

  @Override
  public void addAbstractFactoryRuntimeData(
      AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>> abstractFactoryRuntimeData) {
    abstractFactoryData.put(
        abstractFactoryRuntimeData.getAbstractFactoryClass(),
        abstractFactoryRuntimeData);
  }

  @Override
  public Set<Class<? extends AbstractFactory<?>>> getAbstractFactories() {
    return Collections.unmodifiableSet(abstractFactoryData.keySet());
  }

  @Override
  public AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>> removeAbstractFactoryRuntimeData(
      Class<? extends AbstractFactory<?>> factoryRuntimeData) {
    return abstractFactoryData.remove(factoryRuntimeData);
  }

  @Override
  public FailureTolerance getFailureTolerance() {
    return failureTolerance;
  }

  @Override
  public void setAndStoreFailureTolerance(FailureTolerance failureTolerance)
      throws IOException {
    this.failureTolerance = failureTolerance;
    save();
  }

  @Override
  public synchronized boolean isOpen() {
    return opened;
  }

}
