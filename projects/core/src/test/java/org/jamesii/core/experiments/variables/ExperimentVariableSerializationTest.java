/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables;

import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.modifier.IncrementModifierInteger;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * @author Roland Ewald
 * 
 */
public class ExperimentVariableSerializationTest extends
    SimpleSerializationTest<ExperimentVariable<Integer>> {

  @Override
  public void assertEquality(ExperimentVariable<Integer> original,
      ExperimentVariable<Integer> deserialisedVersion) {
    assertEquals(original.getValue(), deserialisedVersion.getValue());
    assertEquals(original.getName(), deserialisedVersion.getName());
    assertNotNull(deserialisedVersion.getModifier());
    assertTrue(deserialisedVersion.getModifier() instanceof IncrementModifierInteger);
  }

  @Override
  public ExperimentVariable<Integer> getTestObject() throws Exception {
    return new ExperimentVariable<>("testName",
        new IncrementModifierInteger(1, 1, 3));
  }
}
