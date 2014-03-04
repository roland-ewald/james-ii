/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry;


import java.util.ArrayList;
import java.util.Set;

import junit.framework.TestCase;

import org.jamesii.SimSystem;
import org.jamesii.asf.integrationtest.bogus.simulator.BestSimulatorInTheWorldFactory;
import org.jamesii.asf.portfolios.stochsearch.StochSearchPortfolioSelFactory;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.NoFactoryFoundException;
import org.jamesii.core.model.Model;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.DomFactory;
import org.jamesii.core.plugins.IParameter;
import org.jamesii.core.plugins.metadata.FailureTolerance;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;

/**
 * Tests the {@link AlgoSelectionRegistry}.
 * 
 * NOTE: Make sure that this tests runs in its own JVM, as it sets a new
 * Registry at the beginning.
 * 
 * @author Roland Ewald
 * 
 */
public class TestAlgoSelectionRegistry extends TestCase {

  /** The algorithm-selection registry. */
  AlgoSelectionRegistry asr = new AlgoSelectionRegistry();

  /**
   * Tries to establish the {@link AlgoSelectionRegistry} as the system's
   * registry.
   */
  @Override
  public void setUp() {
    SimSystem.setRegistry(asr);
    // Re-assign registry reference - this method is called multiple
    // times, and from the second call on the registry will already have been
    // set (and will not be set again)
    asr = (AlgoSelectionRegistry) SimSystem.getRegistry();
  }

  public void testParameterBlockCopying() {
    ParameterBlock first = new ParameterBlock();
    first.addSubBl("B", new ParameterBlock());
    ParameterBlock second = new ParameterBlock("A");
    second.addSubBl("B", new ParameterBlock("C"));
    second.addSubBl("C", new ParameterBlock("D"));

    AlgoSelectionRegistry.copyValues(first, second);

    assertEquals("A", first.getValue());
    assertEquals(2, first.getSubBlocks().size());
    assertEquals("D", first.getSubBlock("C").getValue());
    assertEquals("C", first.getSubBlock("B").getValue());
  }

  /**
   * Tests
   * {@link AlgoSelectionRegistry#lookupFactories(org.jamesii.core.parameters.ParameterBlock)}
   * .
   */
  public void testFactoryLookup() {

    assertEquals(0, asr.lookupFactories(null).size());
    assertEquals(0, asr.lookupFactories(new ParameterBlock()).size());

    ParameterBlock pb = new ParameterBlock();
    pb.addSubBl("test",
        new ParameterBlock(StochSearchPortfolioSelFactory.class.getName()));

    Set<Class<? extends Factory<?>>> facs = asr.lookupFactories(pb);
    assertEquals(1, facs.size());
    assertEquals(StochSearchPortfolioSelFactory.class, facs.iterator().next());
  }

  /**
   * Tests persistence of configured failure tolerance.
   */
  public void testFailureTolerancePersistence() {
    FailureTolerance oldTolerance = asr.getFailureTolerance();
    asr.setAndStoreFailureTolerance(FailureTolerance.ACCEPT_ALL);
    AlgoSelectionRegistry newASR = new AlgoSelectionRegistry();
    newASR.init();
    assertEquals(FailureTolerance.ACCEPT_ALL, newASR.getFailureTolerance());
    asr.setAndStoreFailureTolerance(oldTolerance);
    assertEquals(oldTolerance, asr.getFailureTolerance());
  }

  /**
   * Test failure tolerance setting.
   */
  public void testFailureTolerance() throws Exception {

    asr.setAndStoreFailureTolerance(FailureTolerance.ACCEPT_UNTESTED);
    ParameterBlock parameters = createFactoryInfoAndParameters();
    boolean nffExceptionCaught = false;
    try {
      asr.getFactory(AbstractProcessorFactory.class, parameters);
    } catch (NoFactoryFoundException e) {
      nffExceptionCaught = true;
    }
    assertTrue(nffExceptionCaught);

    nffExceptionCaught = false;
    asr.setAndStoreFailureTolerance(FailureTolerance.ACCEPT_ALL);
    try {
      asr.getFactory(AbstractProcessorFactory.class, parameters);
    } catch (NoFactoryFoundException e) {
      nffExceptionCaught = true;
    }
    assertFalse(nffExceptionCaught);
  }

  /**
   * Creates the factory info and parameters.
   * 
   * @return the parameter block
   * 
   * @throws Exception
   *           the exception
   */
  private ParameterBlock createFactoryInfoAndParameters() throws Exception {
    DomFactory domFactory = new DomFactory();
    domFactory.setClassname(BestSimulatorInTheWorldFactory.class.getName());
    domFactory.setParameters(new ArrayList<IParameter>());

    asr.addNewFactoryManually(new BestSimulatorInTheWorldFactory(), domFactory);

    ParameterBlock parameters = new ParameterBlock();
    parameters.addSubBl(AbstractProcessorFactory.PARTITION, new Partition(
        new Model() {
          private static final long serialVersionUID = -2162741150899329228L;
        }, null, null));
    return parameters;
  }
}
