/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner;

import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.taskrunner.ComputationTaskHandler;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;
import org.jamesii.core.simulation.launch.InteractiveConsole;

import junit.framework.TestCase;

/**
 * Tests for the {@link ComputationTaskHandler}.
 * 
 * @author Stefan Leye
 */
public class TestComputationTaskHandler extends TestCase {

  /**
   * Test init simulation. As implemented for now, this will still cause an NPE
   * in another thread (so this test does not fail), since no model is
   * specified.
   */
  public void testInitComputationTask() {
    TaskConfiguration config = new TaskConfiguration();
    IInitializedComputationTask initSim = null;
    SimulationRunConfiguration srConfig =
        (SimulationRunConfiguration) config
            .newComputationTaskConfiguration(new ComputationTaskIDObject());
    initSim =
        ComputationTaskHandler.initComputationTask(srConfig, null,
            new RunInformation(srConfig), new StringBuffer(), null);
    assertNotNull(initSim);
  }

  /**
   * Test run simulation.
   */
  public void testRunComputationTask() {
    TaskConfiguration config = new TaskConfiguration();
    IInitializedComputationTask initSim = null;
    SimulationRunConfiguration srConfig =
        (SimulationRunConfiguration) config
            .newComputationTaskConfiguration(new ComputationTaskIDObject());
    initSim =
        ComputationTaskHandler.initComputationTask(srConfig, null,
            new RunInformation(srConfig), new StringBuffer(), null);
    assertNotNull(initSim);

    RunInformation runInfo =
        ComputationTaskHandler.runComputationTask(initSim, srConfig,
            new InteractiveConsole(), new StringBuffer(), null);
    assertNotNull(runInfo);
  }
}
