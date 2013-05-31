/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

/**
 * All state in the life time of a computation job that are of interest to the
 * user.
 * 
 * @author Roland Ewald
 */
public enum ComputationRuntimeState {

  /** Job is cancelled. */
  CANCELLED,

  /** Job is finished. */
  FINISHED,

  /** Job has been initialised. */
  INITIALIZED,

  /** Job is paused. */
  PAUSED,

  /** Job is running. */
  RUNNING

}
