/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables;

/**
 * Distinctions regarding the domain of an experiment variable.
 * 
 * @author Roland Ewald
 */
public enum ExperimentVariableDomain {

  /**
   * It is a model variable, to be changed during an experiment.
   */
  MODEL,

  /**
   * It is a variable regarding a model's execution (ie, simulation), to be
   * changed during the experiment.
   */
  EXECUTION

}
