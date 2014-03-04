/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.failuredetection;

import org.jamesii.core.plugins.metadata.ComponentState;
import org.jamesii.core.plugins.metadata.FailureTolerance;

import junit.framework.TestCase;

/**
 * Tests failure tolerance.
 * 
 * @see FailureTolerance
 * 
 * @author Roland Ewald
 * 
 */
public class TestFailureTolerance extends TestCase {

  /**
   * Test tolerance.
   */
  public void testTolerance() {
    for (ComponentState state : ComponentState.values()) {
      assertTrue(FailureTolerance.ACCEPT_ALL.accept(state));
    }

    assertTrue(FailureTolerance.ACCEPT_UNTESTED.accept(ComponentState.UNTESTED));
    assertFalse(FailureTolerance.ACCEPT_UNTESTED.accept(ComponentState.BROKEN));

    assertTrue(FailureTolerance.ACCEPT_TESTED.accept(ComponentState.STABLE));
    assertFalse(FailureTolerance.ACCEPT_TESTED.accept(ComponentState.UNTESTED));

    assertTrue(FailureTolerance.ACCEPT_STABLE.accept(ComponentState.STABLE));
    assertFalse(FailureTolerance.ACCEPT_STABLE.accept(ComponentState.TESTED));
  }
}
