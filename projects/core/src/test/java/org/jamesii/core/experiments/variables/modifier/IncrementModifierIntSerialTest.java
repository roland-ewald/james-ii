/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.variables.modifier.IncrementModifierInteger;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * Simple serialisation test for {@link IncrementModifierInteger}.
 * 
 * @author Roland Ewald
 * 
 */
public class IncrementModifierIntSerialTest extends
    SimpleSerializationTest<IncrementModifierInteger> {

  @Override
  public void assertEquality(IncrementModifierInteger original,
      IncrementModifierInteger deserialisedVersion) {
    assertEquals(original.getIncrementBy(),
        deserialisedVersion.getIncrementBy());
    assertEquals(original.getStartValue(), deserialisedVersion.getStartValue());
    assertEquals(original.getStopValue(), deserialisedVersion.getStopValue());
  }

  @Override
  public IncrementModifierInteger getTestObject() throws Exception {
    return new IncrementModifierInteger(1, 1, 10);
  }

}
