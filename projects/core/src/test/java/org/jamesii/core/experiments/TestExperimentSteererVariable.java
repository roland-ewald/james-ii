/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import org.jamesii.core.experiments.optimization.MockOptProblemDef;
import org.jamesii.core.experiments.optimization.MockOptimizationAlgorithm;
import org.jamesii.core.experiments.optimization.Optimizer;
import org.jamesii.core.experiments.steering.ExperimentSteererVariable;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * Tests {@link ExperimentSteererVariable}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestExperimentSteererVariable extends
    SimpleSerializationTest<ExperimentSteererVariable<Optimizer>> {

  @Override
  public void assertEquality(ExperimentSteererVariable<Optimizer> original,
      ExperimentSteererVariable<Optimizer> deserialisedVersion) {
    assertEquals(original.getSteererClass(),
        deserialisedVersion.getSteererClass());
    assertEquals(original.getValue().getClass(), deserialisedVersion.getValue()
        .getClass());
  }

  @Override
  public ExperimentSteererVariable<Optimizer> getTestObject() throws Exception {
    Optimizer opt =
        new Optimizer(new MockOptimizationAlgorithm(), new MockOptProblemDef());
    return new ExperimentSteererVariable<>("expSteerer", Optimizer.class, opt,
        new SequenceModifier<>(new Optimizer[] { opt }));
  }
}
