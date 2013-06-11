/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata;

/**
 * Enumerates different modes of failure tolerance to be supported.
 * 
 * @author Roland Ewald
 */
public enum FailureTolerance {

  /**
   * No restrictions, even broken components or those under development are
   * selected.
   */
  ACCEPT_ALL,

  /** Accept untested, tested, or stable components. */
  ACCEPT_UNTESTED,

  /** Accept tested or stable components. */
  ACCEPT_TESTED,

  /** Accept stable elements only. */
  ACCEPT_STABLE;

  /**
   * Tests whether current tolerance accepts the component with the given state.
   * 
   * @param state
   *          the state of the component
   * 
   * @return true, if successful
   */
  public boolean accept(ComponentState state) {
    switch (this) {
    case ACCEPT_ALL:
      return true;
    case ACCEPT_UNTESTED:
      return ComponentState.UNTESTED.compareTo(state) <= 0;
    case ACCEPT_TESTED:
      return ComponentState.TESTED.compareTo(state) <= 0;
    case ACCEPT_STABLE:
      return ComponentState.STABLE.compareTo(state) <= 0;
    }
    return false;
  }

}
