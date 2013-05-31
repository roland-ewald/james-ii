/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * Tests for {@link BaseExperiment}.
 * 
 * @author Roland Ewald
 * @author Simon Bartels
 */
public class BaseExperimentSerializationTest extends
    SimpleSerializationTest<BaseExperiment> {

  @Override
  public void assertEquality(BaseExperiment original,
      BaseExperiment deserialisedVersion) {
    assertTrue(original.getParameters().getParameterBlock()
        .compareTo(deserialisedVersion.getParameters().getParameterBlock()) == 0);
  }

  @Override
  public BaseExperiment getTestObject() throws Exception {
    BaseExperiment exp = new BaseExperiment();
    exp.setProcessorFactoryParameters( "test.ProcessorFactory");
    return exp;
  }
}
