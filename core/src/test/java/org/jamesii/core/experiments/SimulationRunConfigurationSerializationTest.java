/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * @author Roland Ewald
 * 
 */
public class SimulationRunConfigurationSerializationTest extends
    SimpleSerializationTest<SimulationRunConfiguration> {

  @Override
  public void assertEquality(SimulationRunConfiguration original,
      SimulationRunConfiguration deserialisedVersion) {
    assertEquals(original.getExperimentID(),
        deserialisedVersion.getExperimentID());
  }

  @Override
  public SimulationRunConfiguration getTestObject() throws Exception {
    return (SimulationRunConfiguration) new SimulationConfigurationSerializationTest()
        .getTestObject().newComputationTaskConfiguration(
            new ComputationTaskIDObject());
  }

}
