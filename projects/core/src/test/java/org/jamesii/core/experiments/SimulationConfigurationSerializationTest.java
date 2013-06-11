/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.ArrayList;
import java.util.HashMap;

import org.jamesii.core.cmdparameters.Parameters;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.core.util.id.UniqueIDGenerator;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * Tests if {@link TaskConfiguration} is serializable.
 * 
 * @author Roland Ewald
 * 
 */
public class SimulationConfigurationSerializationTest extends
    SimpleSerializationTest<TaskConfiguration> {

  @Override
  public void assertEquality(TaskConfiguration original,
      TaskConfiguration deserialisedVersion) {
    assertEquals(0, original.compareTo(deserialisedVersion));
  }

  @Override
  public TaskConfiguration getTestObject() throws Exception {
    TaskConfiguration sc =
        new TaskConfiguration(2, new ParameterBlock(),
            new HashMap<String, Object>(),
            (new Parameters()).getParameterBlock());
    sc.setExperimentID(UniqueIDGenerator.createUniqueID());
    sc.setConfigNumber(2);
    sc.setSimStartTime(1.23);
    sc.setInterStepDelay(234);
    sc.setStartPaused(true);
    sc.setComputationTaskStopFactory(new ParameterizedFactory<ComputationTaskStopPolicyFactory>(
        new SimTimeStopFactory(), new ParameterBlock()));
    sc.setSimulationObservers(new ArrayList<IObserver<? extends IObservable>>());
    return sc;
  }

}
