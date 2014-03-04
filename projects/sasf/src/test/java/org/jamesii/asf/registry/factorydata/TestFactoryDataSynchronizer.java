/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.factorydata;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jamesii.asf.registry.factorydata.file.TestRegFileDS;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.distributions.BinomialDistributionFactory;
import org.jamesii.core.math.random.distributions.ExponentialDistributionFactory;
import org.jamesii.core.math.random.distributions.NormalDistributionFactory;
import org.jamesii.core.math.random.distributions.PoissonDistributionFactory;
import org.jamesii.core.math.random.distributions.plugintype.AbstractDistributionFactory;
import org.jamesii.core.plugins.metadata.FactoryDataSynchronizer;
import org.jamesii.core.plugins.metadata.IFactoryRuntimeDataStorage;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.util.eventset.plugintype.AbstractEventQueueFactory;

import junit.framework.TestCase;

/**
 * Tests for the {@link FactoryDataSynchronizer}.
 * 
 * @author Roland Ewald
 */
public class TestFactoryDataSynchronizer extends TestCase {

  /**
   * The Constant TRIALS. Determines how often addition/removal shall be tested
   * in repetition.
   */
  private static final int TRIALS = 10;

  /** The data storage. */
  private IFactoryRuntimeDataStorage dataStorage;

  @Override
  public void setUp() throws Exception {
    dataStorage = (new TestRegFileDS()).getFactoryRuntimeDataStorage();
    dataStorage.reset();
  }

  public void testEmptyNewListFactories() throws Exception {
    assertEquals(0, dataStorage.getFactories().size());
    FactoryDataSynchronizer.addNewFactories(new ArrayList<Factory<?>>(),
        dataStorage);
    assertEquals(0, dataStorage.getFactories().size());
  }

  public void testEmptyNewListAbsFactories() throws Exception {
    assertEquals(0, dataStorage.getAbstractFactories().size());
    FactoryDataSynchronizer.addNewAbstractFactories(
        new HashSet<Class<? extends AbstractFactory<?>>>(), dataStorage);
    assertEquals(0, dataStorage.getAbstractFactories().size());
  }

  public void testNewFactories() throws Exception {
    List<Factory<?>> factories = new ArrayList<>();
    factories.add(new NormalDistributionFactory());
    for (int i = 0; i < TRIALS; i++) {
      FactoryDataSynchronizer.addNewFactories(factories, dataStorage);
      assertEquals(1, dataStorage.getFactories().size());
    }
  }

  public void testNewAbstrFactories() throws Exception {
    Set<Class<? extends AbstractFactory<?>>> absFactories =
        new HashSet<>();
    absFactories.add(AbstractProcessorFactory.class);
    for (int i = 0; i < TRIALS; i++) {
      FactoryDataSynchronizer
          .addNewAbstractFactories(absFactories, dataStorage);
      assertEquals(1, dataStorage.getAbstractFactories().size());
    }
  }

  public void testRemoveFactories() throws Exception {
    List<Factory<?>> factories = new ArrayList<>();
    factories.add(new NormalDistributionFactory());
    factories.add(new ExponentialDistributionFactory());
    FactoryDataSynchronizer.addNewFactories(factories, dataStorage);
    factories.remove(1);
    for (int i = 0; i < TRIALS; i++) {
      FactoryDataSynchronizer.removeOldFactories(factories, dataStorage);
      assertEquals(1, dataStorage.getFactories().size());
      assertEquals(NormalDistributionFactory.class, dataStorage.getFactories()
          .iterator().next());
    }
  }

  public void testRemoveAbstractFactories() throws Exception {
    Set<Class<? extends AbstractFactory<?>>> absFactories =
        new HashSet<>();
    absFactories.add(AbstractProcessorFactory.class);
    absFactories.add(AbstractDistributionFactory.class);
    FactoryDataSynchronizer.addNewAbstractFactories(absFactories, dataStorage);
    absFactories.remove(AbstractDistributionFactory.class);
    for (int i = 0; i < TRIALS; i++) {
      FactoryDataSynchronizer.removeOldAbstractFactories(absFactories,
          dataStorage);
      assertEquals(1, dataStorage.getAbstractFactories().size());
      assertEquals(AbstractProcessorFactory.class, dataStorage
          .getAbstractFactories().iterator().next());
    }
  }

  public void testFactoriesSynchronisation() throws Exception {

    List<Factory<?>> oldFactories = new ArrayList<>();
    oldFactories.add(new NormalDistributionFactory());
    oldFactories.add(new ExponentialDistributionFactory());
    FactoryDataSynchronizer.synchroniseFactories(oldFactories, dataStorage);
    assertEquals(2, dataStorage.getFactories().size());

    List<Factory<?>> newFactories = new ArrayList<>();
    newFactories.add(new PoissonDistributionFactory());
    newFactories.add(new BinomialDistributionFactory());

    for (int i = 0; i < TRIALS; i++) {
      FactoryDataSynchronizer.synchroniseFactories(newFactories, dataStorage);
      assertEquals(2, dataStorage.getFactories().size());
      assertTrue(dataStorage.getFactories().contains(
          PoissonDistributionFactory.class));
      assertTrue(dataStorage.getFactories().contains(
          BinomialDistributionFactory.class));
    }
  }

  public void testAbstractFactoriesSynchronisation() throws Exception {

    Set<Class<? extends AbstractFactory<?>>> oldFactories =
        new HashSet<>();
    oldFactories.add(AbstractProcessorFactory.class);
    oldFactories.add(AbstractDistributionFactory.class);
    FactoryDataSynchronizer.synchroniseAbstractFactories(oldFactories,
        dataStorage);
    assertEquals(2, dataStorage.getAbstractFactories().size());

    Set<Class<? extends AbstractFactory<?>>> newFactories =
        new HashSet<>();
    newFactories.add(AbstractEventQueueFactory.class);
    newFactories.add(AbstractModelReaderFactory.class);

    for (int i = 0; i < TRIALS; i++) {
      FactoryDataSynchronizer.synchroniseAbstractFactories(newFactories,
          dataStorage);
      assertEquals(2, dataStorage.getAbstractFactories().size());
      assertTrue(dataStorage.getAbstractFactories().contains(
          AbstractEventQueueFactory.class));
      assertTrue(dataStorage.getAbstractFactories().contains(
          AbstractModelReaderFactory.class));
    }
  }
}
