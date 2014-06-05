/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;

import examples.devs.IntJamesState;

/**
 * The Class MatchState.
 */
public class MatchState extends IntJamesState {

  // The match is extinguished, this phase is used after
  // the match has initiated the fire
  /** The EXTINGUISHED. */
  public static int EXTINGUISHED = 7;

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4645692748456958407L;

  /**
   * Instantiates a new match state.
   * 
   * @param phase
   *          the phase
   */
  public MatchState(int phase) {
    super(phase);
  }

}
