/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.abort;

import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;

/**
 * Abort after a per-defined wall-clock time (measured as time span that starts
 * with the instantiation of AbortCriterion object).
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class WallClockTimeAbort implements IAbortCriterion {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6577245732708906193L;

  /** The wall-clock end time. */
  private final long wcEndTime;

  /**
   * Instantiates a new wall clock time abort.
   * 
   * @param stopTimeDelta
   *          the time to run the simulation.
   */
  public WallClockTimeAbort(long stopTimeDelta) {
    wcEndTime = System.currentTimeMillis() + stopTimeDelta;
  }

  @Override
  public boolean abort(GeneticAlgorithmPortfolioSelector selector) {
    return System.currentTimeMillis() >= wcEndTime;
  }
}
