/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import org.joone.engine.FullSynapse;
import org.joone.engine.Synapse;

/**
 * Different types of synapses.
 * 
 * @author Roland Ewald
 */
public enum NNSynapse {

  /** Full synapse (all-to-all). */
  FULL;

  /**
   * Creates new synapse instances.
   * 
   * @param synapseType
   * @return
   */
  public static Synapse newInstance(NNSynapse synapseType) {
    return new FullSynapse();
  }

  /**
   * Get type by a number (to support integer parameter).
   * 
   * @param code
   *          the integer code identifying the synapse type
   * @return the corresponding synapse type
   */
  public static NNSynapse getSynapseType(int code) {
    return values()[code];
  }
}
