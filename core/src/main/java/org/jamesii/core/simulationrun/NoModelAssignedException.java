/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun;

/**
 * 
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */

public class NoModelAssignedException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 3688278857617123776L;

  /**
   * Instantiates a new no model assigned exception.
   * 
   * @param s
   *          the s
   */
  public NoModelAssignedException(String s) {
    super(s);
  }

}
