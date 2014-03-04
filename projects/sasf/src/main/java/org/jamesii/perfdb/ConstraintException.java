/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;

/**
 * Exception that is thrown when data constraint violations have been detected.
 * 
 * @author Roland Ewald
 * 
 */
public class ConstraintException extends RuntimeException {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8009708937364069020L;

  /**
   * Default constructor.
   * 
   * @param msg
   *          the message describing the violation
   */
  public ConstraintException(String msg) {
    super(msg);
  }
}
