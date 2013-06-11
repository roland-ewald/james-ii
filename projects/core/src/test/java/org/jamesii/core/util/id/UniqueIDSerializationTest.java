/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.id;

import org.jamesii.core.util.id.UniqueID;
import org.jamesii.core.util.id.UniqueIDGenerator;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * @author Stefan Rybacki
 * 
 */
public class UniqueIDSerializationTest extends
    SimpleSerializationTest<UniqueID> {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  public void assertEquality(UniqueID original, UniqueID deserialisedVersion) {
    assertEquals(original, deserialisedVersion);
  }

  @Override
  public UniqueID getTestObject() throws Exception {
    return (UniqueID) UniqueIDGenerator.createUniqueID();
  }

}
