package org.jamesii.core.experiments.variables;

/**
 * Defines the status of a sub-level. Either it comes up with a new parameter
 * setup, it is done, or it needs additional information.
 * 
 * @see ExperimentVariables
 * 
 * @author Roland Ewald
 */
public enum SubLevelStatus {

  /**
   * The experiment variables have another parameter setup that shall be
   * processed.
   */
  HAS_NEXT,

  /**
   * The experiment variables do *not* have another parameter setup to be
   * processed.
   */
  DONE,

  /**
   * The experiment variables *may* have another parameter setup in the future,
   * but they need additional information which has not been generated yet.
   * Hence it suggests to WAIT.
   */
  WAIT

}
