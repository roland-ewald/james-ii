/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

/**
 * The Enum ProcessorStatus. To be used inside of IRunnable implementation to
 * indicate what's currently going on in the simulation algorithm.
 * 
 * @author Jan Himmelspach
 */
public enum ProcessorStatus {

  /** Initializing. Before running. */
  INITIALIZING,
  /** Running. After calling one of the run methods. */
  RUNNING,
  /**
   * Pausing (can be replaced by "STEPPING"). After calling one of the run
   * methods, and pausing.
   */
  PAUSING,
  /**
   * The stepping (on top of "PAUSING", thus if "STEPPING" "PAUSING" is valid as
   * well).
   */
  STEPPING,
  /** Stopping. About to stop as soon as possible */
  STOPPING,
  /** Stopped. Cannot recover. */
  STOPPED

}
