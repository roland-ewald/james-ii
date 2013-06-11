/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun;

/**
 * The mode of the simulation run. Synchronization with wall-clock time.
 */
public enum Mode {

  /** Paced simulation, simulation time proceeds in relation to wall-clock time. */
  PACED,

  /** Unpaced simulation, as fast as possible. */
  UNPACED;

  @Override
  public String toString() {
    switch (this) {
    case UNPACED:
      return "unpaced";
    case PACED:
      return "paced";
    }
    return "";
  }
}