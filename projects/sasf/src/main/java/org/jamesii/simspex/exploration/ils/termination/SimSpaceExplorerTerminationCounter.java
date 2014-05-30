/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.termination;

import org.jamesii.simspex.exploration.ils.explorer.ILSSimSpaceExplorer;


/**
 * The Class SimSpaceExplorerTerminationCounter. It counts how often the update
 * method was called.
 * 
 * @author Robert Engelke
 */
public class SimSpaceExplorerTerminationCounter implements
    ITerminationIndicator<ILSSimSpaceExplorer> {

  /** The Constant DEFAULT_NUM_OF_EXECUTIONS. */
  private static final int DEFAULT_NUM_OF_EXECUTIONS = 10000;

  /** The termination counter. */
  private int terminationCounter;

  /**
   * Instantiates a new sim space explorer termination counter.
   */
  public SimSpaceExplorerTerminationCounter() {
    terminationCounter = DEFAULT_NUM_OF_EXECUTIONS;
  }

  /**
   * Instantiates a new sim space explorer termination counter.
   * 
   * @param numberOfExecutions
   *          the number of executions
   */
  public SimSpaceExplorerTerminationCounter(int numberOfExecutions) {
    terminationCounter = numberOfExecutions;
  }

  @Override
  public boolean shallTerminate(ILSSimSpaceExplorer element) {
    return terminationCounter <= 0;
  }

  @Override
  public void updateTerminationCriterion(ILSSimSpaceExplorer element) {
    terminationCounter--;
  }

}
