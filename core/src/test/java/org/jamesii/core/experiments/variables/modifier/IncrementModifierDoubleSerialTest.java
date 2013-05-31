/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.variables.modifier.IncrementModifierDouble;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * Simple serialisation test for {@link IncrementModifierDouble}.
 * 
 * @author Roland Ewald
 * 
 */
public class IncrementModifierDoubleSerialTest extends
    SimpleSerializationTest<IncrementModifierDouble> {

  @Override
  public void assertEquality(IncrementModifierDouble original,
      IncrementModifierDouble deserialisedVersion) {
    assertEquals(original.getIncrementBy(),
        deserialisedVersion.getIncrementBy());
    assertEquals(original.getStartValue(), deserialisedVersion.getStartValue());
    assertEquals(original.getStopValue(), deserialisedVersion.getStopValue());
  }

  @Override
  public IncrementModifierDouble getTestObject() throws Exception {
    return new IncrementModifierDouble(1.0, 1.0, 10.0);
  }

}
