/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry;

import org.jamesii.core.plugins.metadata.ComponentAction;
import org.jamesii.core.plugins.metadata.ComponentState;

import junit.framework.TestCase;

/**
 * Simple test for checking the state transition invoked by applying an
 * {@link ComponentAction} to a {@link ComponentState}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestComponentState extends TestCase {

  /**
   * Tests state transitions.
   */
  public void testComponentLifeCycleTransitions() {

    ComponentState myState = ComponentState.UNDER_DEVELOPMENT;

    myState = ComponentState.change(myState, ComponentAction.SUBMIT);
    assertEquals(ComponentState.UNTESTED, myState);

    myState = ComponentState.change(myState, ComponentAction.FINISH_TEST);
    assertEquals(ComponentState.TESTED, myState);

    myState = ComponentState.change(myState, ComponentAction.IS_STABLE);
    assertEquals(ComponentState.STABLE, myState);

    myState = ComponentState.change(myState, ComponentAction.DECLARE_BROKEN);
    assertEquals(ComponentState.BROKEN, myState);

    myState = ComponentState.change(myState, ComponentAction.FIX);
    assertEquals(ComponentState.UNTESTED, myState);

    myState = ComponentState.change(myState, ComponentAction.IS_STABLE);
    assertEquals(ComponentState.STABLE, myState);

    myState = ComponentState.change(myState, ComponentAction.WITHDRAW);
    assertEquals(ComponentState.UNDER_DEVELOPMENT, myState);
  }
}
