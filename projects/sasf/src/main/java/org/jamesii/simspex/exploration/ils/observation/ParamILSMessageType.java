/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.observation;

/**
 * The MessageTypes supported by the ParamILSObserver.
 * 
 * @author Robert Engelke
 */
public enum ParamILSMessageType {

  /** New minimum discovered. */
  MinimumChanged,

  /** Performance estimation of actual minimum changed. */
  MinimumMaintained,

  /** One round of the iterative improvement was finished. */
  IterativeImprovement,

  /** A search restart occurred. */
  Restart,

  /** A new paramILS round was started. */
  ParameterAdjusting,

  /** The ParameterConfiguration calculated. */
  PerformanceEstimation
}