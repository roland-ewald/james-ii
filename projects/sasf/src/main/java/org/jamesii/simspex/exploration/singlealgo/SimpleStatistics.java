/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;

/**
 * Simple structure to hold some result statistics.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleStatistics {

  /** The description of the compared configuration. */
  private final PerfStatsAlgo comparedConfig;

  /** The description of the compared configuration (old data). */
  private final PerfStatsAlgo oldComparedConfig;

  /**
   * The description of the best-performing configuration containing the new
   * algorithm version.
   */
  private final PerfStatsAlgo bestNew;

  /**
   * The description of the best-performing configuration containing the old
   * algorithm version.
   */
  private final PerfStatsAlgo bestOld;

  /**
   * The description of the worst-performing configuration containing the new
   * algorithm version.
   */
  private final PerfStatsAlgo worstNew;

  /**
   * The description of the worst-performing configuration containing the old
   * algorithm version.
   */
  private final PerfStatsAlgo worstOld;

  /**
   * Instantiates a new simple statistics.
   * 
   * @param comparison
   *          the comparison
   * 
   * @param comparison
   *          the old comparison performance
   * @param bNew
   *          the best new performance
   * @param bOld
   *          the best old performance
   * @param wNew
   *          the worst new performance
   * @param wOld
   *          the worst old performance
   * @param worstOld2
   */
  public SimpleStatistics(PerfStatsAlgo comparison,
      PerfStatsAlgo comparisonOld, PerfStatsAlgo bNew, PerfStatsAlgo bOld,
      PerfStatsAlgo wNew, PerfStatsAlgo wOld) {
    comparedConfig = comparison;
    oldComparedConfig = comparisonOld;
    bestNew = bNew;
    bestOld = bOld;
    worstNew = wNew;
    worstOld = wOld;
  }

  public PerfStatsAlgo getComparedConfig() {
    return comparedConfig;
  }

  public PerfStatsAlgo getOldComparedConfig() {
    return oldComparedConfig;
  }

  public PerfStatsAlgo getBestNew() {
    return bestNew;
  }

  public PerfStatsAlgo getBestOld() {
    return bestOld;
  }

  public PerfStatsAlgo getWorstNew() {
    return worstNew;
  }

  public PerfStatsAlgo getWorstOld() {
    return worstOld;
  }

}
