/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;


/**
 * Signals that accessing the database in a speecific manner has failed.
 * 
 * @author Roland Ewald
 * 
 */
public class DatabaseAccessException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5315218265642514864L;

  /**
   * Instantiates a new database access exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the actual cause (if available)
   */
  public DatabaseAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new database access exception.
   * 
   * @param cause
   *          the actual cause
   */
  public DatabaseAccessException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new database access exception.
   * 
   * @param message
   *          the message
   */
  public DatabaseAccessException(String message) {
    super(message);
  }
}
