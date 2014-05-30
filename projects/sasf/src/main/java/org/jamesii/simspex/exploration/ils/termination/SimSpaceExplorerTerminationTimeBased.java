/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.termination;

import org.jamesii.simspex.exploration.ils.explorer.ILSSimSpaceExplorer;


/**
 * The Class SimSpaceExplorerTerminationTimeBased. The update method works on
 * the last RunInformation provided by the ILSSimSpaceExplorer.
 */
public class SimSpaceExplorerTerminationTimeBased implements
    ITerminationIndicator<ILSSimSpaceExplorer> {

  /** The Constant DEFAUL_TTL. */
  static final long DEFAUL_TTL = 36000;

  /** The ttl. */
  private long ttl;

  /**
   * Instantiates a new sim space explorer termination time based.
   */
  public SimSpaceExplorerTerminationTimeBased() {
    ttl = DEFAUL_TTL;
  }

  /**
   * Instantiates a new sim space explorer termination time based.
   * 
   * @param ttl
   *          the overall time of simulation
   */
  public SimSpaceExplorerTerminationTimeBased(long ttl) {
    this.ttl = ttl;
  }

  @Override
  public boolean shallTerminate(ILSSimSpaceExplorer element) {
    return ttl <= 0;
  }

  @Override
  public void updateTerminationCriterion(ILSSimSpaceExplorer element) {
    ttl -= element.getLastRun().getComputationTaskRunTimeInMS();
  }

}
