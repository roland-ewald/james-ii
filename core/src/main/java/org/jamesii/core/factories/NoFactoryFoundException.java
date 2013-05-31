/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

/**
 * This exception is thrown by an AbstractFactory instance if no factory in the
 * current selection process could be determined.
 * 
 * @author Jan Himmelspach
 */
public class NoFactoryFoundException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1214220458632872951L;

  /**
   * Instantiates a new exception.
   */
  public NoFactoryFoundException() {
    super();
  }

  /**
   * Instantiates a new exception.
   * 
   * @param message
   *          the message
   */
  public NoFactoryFoundException(String message) {
    super(message);
  }

  /**
   * Instantiates a new exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public NoFactoryFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new exception.
   * 
   * @param cause
   *          the cause
   */
  public NoFactoryFoundException(Throwable cause) {
    super(cause);
  }

}
