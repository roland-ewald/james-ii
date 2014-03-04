/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.serialization;

import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.id.UniqueIDGenerator;
import org.jamesii.core.util.misc.SimpleSerializationTest;
import org.jamesii.simspex.util.JamesSimDataProvider;


/**
 * Tests whether {@link JamesSimDataProvider} is serialisable.
 * 
 * @author Roland Ewald
 * 
 */
public class J2SimDataProvSerialTest extends
    SimpleSerializationTest<JamesSimDataProvider<String>> {

  @Override
  public JamesSimDataProvider<String> getTestObject() {
    IComputationTaskConfiguration simRunConfig =
        (new TaskConfiguration())
            .newComputationTaskConfiguration(new ComputationTaskIDObject(2L));
    RunInformation runInfo = new RunInformation(simRunConfig);
    runInfo.setExpID(UniqueIDGenerator.createUniqueID());
    runInfo.setDataStorageFactory(DummyDataStorageFactory.class);
    runInfo.setDataStorageParams(new ParameterBlock());
    return new MyJamesSimDataProvider(runInfo);
  }

  @Override
  public void assertEquality(JamesSimDataProvider<String> original,
      JamesSimDataProvider<String> deserialisedVersion) {
    assertEquals(original.getExpID(), deserialisedVersion.getExpID());
    assertEquals(original.getTaskID(), deserialisedVersion.getTaskID());
    assertEquals(original.getDsFactory(), deserialisedVersion.getDsFactory());
    assertEquals(
        0,
        original.getDsParameters().compareTo(
            deserialisedVersion.getDsParameters()));
  }
}
