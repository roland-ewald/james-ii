/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integration;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import junit.framework.TestCase;

import org.jamesii.asf.integrationtest.bogus.application.model.BogusModel;
import org.jamesii.asf.integrationtest.bogus.application.simulator.FlexibleBogusSimulatorFactory;
import org.jamesii.asf.integrationtest.bogus.application.simulator.IBogusSimulatorProperties;
import org.jamesii.core.experiments.BaseExperiment;

/**
 * A collection of simple tests for the bogus formalism.
 * 
 * @author Roland Ewald
 */
public class SimpleTestExperiments extends TestCase {

  /**
   * Test simple experiment execution.
   */
  public void testSimpleExperimentExecution() throws Exception {
    BaseExperiment exp = new BaseExperiment();
    exp.setModelLocation(new URI("java://"
        + BogusModel.class.getCanonicalName()));
    exp.setDefaultSimStopTime(100);
    exp.execute();

    for (int load : new int[] { 1, 2, 4, 8, 16, 32, 64 }) {
      executeExperimentWithLoad(load);
    }
  }

  /**
   * Execute experiment with given load.
   * 
   * @param load
   *          the load
   * 
   * @throws Exception
   *           the exception
   */
  private void executeExperimentWithLoad(final int load) throws Exception {
    BaseExperiment exp = new BaseExperiment();
    exp.setModelLocation(new URI("java://"
        + BogusModel.class.getCanonicalName()));
    exp.setDefaultSimStopTime(100);
    exp.setProcessorFactoryParameters(FlexibleBogusSimulatorFactory.class
        .getName());
    exp.getParameters()
        .getParameterBlock()
        .addSubBlock(FlexibleBogusSimulatorFactory.SIM_PROPERTIES,
            new IBogusSimulatorProperties() {
              private static final long serialVersionUID =
                  -8801113732776797227L;

              @Override
              public int getLoadPerSteps(Map<String, Serializable> modelContent) {
                return load;
              }
            });
    exp.execute();
  }

}
