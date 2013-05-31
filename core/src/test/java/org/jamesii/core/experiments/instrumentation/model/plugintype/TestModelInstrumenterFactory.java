/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.model.plugintype;

import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;

import junit.framework.TestCase;

/**
 * Tests for the static methods provided by {@link ModelInstrumenterFactory}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestModelInstrumenterFactory extends TestCase {

  public void testSchemeMatching() {

    // Test with single element
    assertEquals(1, ModelInstrumenterFactory.matchScheme("file-xyz",
        new String[] { "xyz" }));
    assertEquals(0,
        ModelInstrumenterFactory.matchScheme("file-xyz", new String[] { "xy" }));

    // Test with multiple non-matching elements
    assertEquals(
        1,
        ModelInstrumenterFactory.matchScheme("file-xyz", new String[] { "a",
            "b", "xyz", "c" }));
    assertEquals(
        0,
        ModelInstrumenterFactory.matchScheme("file-xyz", new String[] { "a",
            "xy", "b" }));

    // Test error handling
    boolean exceptionCaught = false;
    try {
      ModelInstrumenterFactory.matchScheme("file-",
          new String[] { "should not matter" });
    } catch (IllegalArgumentException ex) {
      exceptionCaught = true;
    }
    assertTrue("An illegal argument exception should have been caught",
        exceptionCaught);
  }
}
