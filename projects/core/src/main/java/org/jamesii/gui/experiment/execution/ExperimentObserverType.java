/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.execution;

/**
 * Different types of observers to be managed by the experiment perspective.
 * 
 * @author Roland Ewald
 */
public enum ExperimentObserverType {

  /** Observes the experiment itself. */
  EXPERIMENT,

  /** Observes model. */
  MODEL,

  /** Observes simulation. */
  SIMULATION

}
