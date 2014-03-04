/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;

import org.jamesii.core.util.misc.Triple;

/**
 * Simple representation of 3-tuple (performance, config_id, config_desc).
 * 
 * @author Roland Ewald
 * 
 */
public class PerfStatsAlgo extends Triple<Double, Long, String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3254834186085940823L;

  /**
   * Instantiates a new tuple of performance statistics for an algorithm.
   * 
   * @param performance
   *          the performance
   * @param configID
   *          the config id
   * @param configDescription
   *          the config description
   */
  public PerfStatsAlgo(Double performance, Long configID,
      String configDescription) {
    super(performance, configID, configDescription);
  }

}
