/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.cacore;

import org.jamesii.core.model.State;

/**
 * The Class CAState, representing a state of a Cellular Automaton cell.
 * 
 * @param <S>
 *          the state type
 */
public abstract class CAState<S> extends State {
  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -2480451533535534487L;

  /**
   * Instantiates a new CA state. Remember that the internal state S must be
   * immutable.
   * 
   * @param defaultState
   *          the default state
   */
  public CAState(S defaultState) {
    super();
    set(defaultState);
  }

  /** The state. */
  private S state;

  /**
   * Gets the actual state hold by the state object
   * 
   * @return the actual state
   */
  public S get() {
    return state;
  }

  /**
   * Sets the actual state for this state object.
   * 
   * @param state
   *          the new state
   */
  public final void set(S state) {
    this.state = state;
    changed();
  }
}
