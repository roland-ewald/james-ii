/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import org.jamesii.core.experiments.optimization.Optimizer;
import org.jamesii.core.experiments.optimization.OptimizerVariable;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * The Class TestOptimizerVariable.
 * 
 * @author Roland Ewald
 */
public class TestOptimizerVariable extends
    SimpleSerializationTest<OptimizerVariable> {

  @Override
  public void assertEquality(OptimizerVariable original,
      OptimizerVariable deserialisedVersion) {
    assertEquals(((SequenceModifier<Optimizer>) original.getModifier())
        .getValues().size(),
        ((SequenceModifier<Optimizer>) deserialisedVersion.getModifier())
            .getValues().size());
  }

  @Override
  public OptimizerVariable getTestObject() throws Exception {
    Optimizer opt =
        new Optimizer(new MockOptimizationAlgorithm(), new MockOptProblemDef());
    return new OptimizerVariable(new Optimizer[] { opt });
  }
}
