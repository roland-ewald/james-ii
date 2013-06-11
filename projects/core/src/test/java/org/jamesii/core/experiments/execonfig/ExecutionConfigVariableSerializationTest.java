/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.execonfig;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.execonfig.ExecutionConfigurationVariable;
import org.jamesii.core.experiments.execonfig.ParamBlockUpdateModifier;
import org.jamesii.core.experiments.execonfig.SingularParamBlockUpdate;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * @author Roland Ewald
 * 
 */
public class ExecutionConfigVariableSerializationTest
    extends
    SimpleSerializationTest<ExecutionConfigurationVariable<SingularParamBlockUpdate>> {

  @Override
  public void assertEquality(
      ExecutionConfigurationVariable<SingularParamBlockUpdate> original,
      ExecutionConfigurationVariable<SingularParamBlockUpdate> deserialisedVersion) {
    assertEquals(original.getName(), deserialisedVersion.getName());
    assertTrue((original.getInitialValue() == null && deserialisedVersion
        .getInitialValue() == null)
        || original.getInitialValue().getContent()
            .compareTo(deserialisedVersion.getInitialValue().getContent()) == 0);
  }

  @Override
  public ExecutionConfigurationVariable<SingularParamBlockUpdate> getTestObject()
      throws Exception {
    List<SingularParamBlockUpdate> rules = new ArrayList<>();
    rules.add(new SingularParamBlockUpdate(new String[] { "a", "b" }, "c",
        new ParameterBlock("d")));
    return new ExecutionConfigurationVariable<>("var",
        new ParamBlockUpdateModifier<>(rules));
  }

}
