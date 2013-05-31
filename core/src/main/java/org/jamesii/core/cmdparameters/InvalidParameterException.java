/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

/**
 * The Class InvalidParameterException. Can be thrown to indicate an invalid /
 * missing parameter.
 */
public class InvalidParameterException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 6218283877955451575L;

  /**
   * Instantiates a new invalid parameter exception.
   */
  public InvalidParameterException() {
    super();

  }

  /**
   * The Constructor.
   * 
   * @param message
   *          the message
   */
  public InvalidParameterException(String message) {
    super(message);

  }

  /**
   * The Constructor.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public InvalidParameterException(String message, Throwable cause) {
    super(message, cause);

  }

  /**
   * The Constructor.
   * 
   * @param cause
   *          the cause
   */
  public InvalidParameterException(Throwable cause) {
    super(cause);

  }

}
