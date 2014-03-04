/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.factorydata.file;

import org.jamesii.core.plugins.metadata.file.RuntimeData;
import org.jamesii.core.util.misc.SimpleSerializationTest;


/**
 * Tests serializbility of {@link RuntimeData}.
 * 
 * @author Roland Ewald
 */
public class TestRuntimeDataSerializability extends
    SimpleSerializationTest<RuntimeData> {

  @Override
  public RuntimeData getTestObject() throws Exception {
    return new RuntimeData(null, null, null);
  }

  @Override
  public void assertEquality(RuntimeData original,
      RuntimeData deserialisedVersion) {
    assertNotNull(deserialisedVersion.getAbstractFactoryData());
    assertNotNull(deserialisedVersion.getFactoryData());
  }

}
