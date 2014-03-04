/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.factorydata;

import org.jamesii.asf.integrationtest.bogus.simulator.BestSimulatorInTheWorldFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.distributions.ExponentialDistributionFactory;
import org.jamesii.core.math.random.distributions.UniformDistributionFactory;
import org.jamesii.core.plugins.metadata.ComponentAction;
import org.jamesii.core.plugins.metadata.ComponentState;
import org.jamesii.core.plugins.metadata.FactoryRuntimeData;
import org.jamesii.core.plugins.metadata.IFactoryRuntimeDataStorage;

import junit.framework.TestCase;

/**
 * General test for implementations of {@link IFactoryRuntimeDataStorage}.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class TestFactoryRuntimeDataStorage extends TestCase {

  /** The data storage to be tested. */
  protected IFactoryRuntimeDataStorage ds;

  @Override
  public void setUp() throws Exception {
    ds = getFactoryRuntimeDataStorage();
    ds.reset();
  }

  /**
   * Tests all basic functions.
   * 
   * @throws Exception
   *           the exception
   */
  public void testBasicFunctions() throws Exception {
    ds.open();

    // Check state change for bogus simulator
    FactoryRuntimeData<? extends Factory<?>> bestSimRTData =
        new FactoryRuntimeData<>(
            BestSimulatorInTheWorldFactory.class);
    ds.addFactoryRuntimeData(bestSimRTData);
    assertEquals(ComponentState.UNDER_DEVELOPMENT,
        ds.getFactoryData(BestSimulatorInTheWorldFactory.class).getState());
    bestSimRTData.success();
    bestSimRTData.changeState(ComponentAction.SUBMIT);
    assertEquals(ComponentState.UNTESTED,
        ds.getFactoryData(BestSimulatorInTheWorldFactory.class).getState());

    // Test ordinary factory
    FactoryRuntimeData<? extends Factory<?>> unifDistRTData =
        new FactoryRuntimeData<>(
            UniformDistributionFactory.class);
    ds.addFactoryRuntimeData(unifDistRTData);
    unifDistRTData.success();
    ds.close();
    ds.open();

    assertEquals(1, ds.getFactoryData(BestSimulatorInTheWorldFactory.class)
        .getSuccessfulExecutions());
    assertEquals(1, ds.getFactoryData(UniformDistributionFactory.class)
        .getSuccessfulExecutions());
    assertEquals(null, ds.getFactoryData(ExponentialDistributionFactory.class));
    assertEquals(ComponentState.UNTESTED,
        ds.getFactoryData(UniformDistributionFactory.class).getState());
  }

  /**
   * Gets the data storage.
   * 
   * @return the data storage
   * 
   * @throws Exception
   *           the exception
   */
  protected abstract IFactoryRuntimeDataStorage getFactoryRuntimeDataStorage()
      throws Exception;
}
