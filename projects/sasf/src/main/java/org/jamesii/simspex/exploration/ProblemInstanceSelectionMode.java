/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration;

/**
 * Modes of problem instance retrieval or generation.
 * 
 * @author Roland Ewald
 * 
 */
public enum ProblemInstanceSelectionMode {

  /** A single constant problem instance is used. */
  CONSTANT,

  /** All defined problem instances are used round-robin. */
  ROUND_ROBIN,

  /** New problem instances are added on the fly. */
  EXPLORATIVE

}
