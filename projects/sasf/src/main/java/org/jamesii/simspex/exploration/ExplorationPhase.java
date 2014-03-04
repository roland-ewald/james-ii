/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration;

/**
 * Represents the different phases of an exploration.
 * 
 * @author Roland Ewald
 * 
 */
public enum ExplorationPhase {

  /** The calibration is started. */
  START_CALIBRATION,

  /** The calibration phase. */
  CALIBRATION,

  /** The exploration phase. */
  EXPLORATION;

  @Override
  public String toString() {
    switch (this) {
    case START_CALIBRATION:
      return "START_CALIBRATION";
    case CALIBRATION:
      return "CALIBRATION";
    case EXPLORATION:
      return "EXPLORATION";
    default:
      return "UNKNOWN";
    }
  }
}
