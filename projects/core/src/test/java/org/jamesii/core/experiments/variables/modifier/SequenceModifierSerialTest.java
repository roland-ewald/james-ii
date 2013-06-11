/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.variables.modifier.MathModifierInteger;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * Simple serialisation test for {@link MathModifierInteger}.
 * 
 * @author Roland Ewald
 * 
 */
public class SequenceModifierSerialTest extends
    SimpleSerializationTest<SequenceModifier<Integer>> {

  @Override
  public void assertEquality(SequenceModifier<Integer> original,
      SequenceModifier<Integer> deserialisedVersion) {
    assertEquals(original.getValues(), deserialisedVersion.getValues());
  }

  @Override
  public SequenceModifier<Integer> getTestObject() throws Exception {
    List<Integer> testValues = new ArrayList<>(1);
    testValues.add(1);
    return new SequenceModifier<>(testValues);
  }

}
