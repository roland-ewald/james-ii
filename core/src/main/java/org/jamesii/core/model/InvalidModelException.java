/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model;

/**
 * InvalidModelException. This exception should be used whenever a model is
 * invalid - e.g., if another model type was expected in a method - or if
 * something else is wrong with the model.
 * 
 * @author Jan Himmelspach
 */
public class InvalidModelException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2100837481215386219L;

  /**
   * Instantiates a new invalid model exception.
   * 
   * @param message
   *          the message
   */
  public InvalidModelException(String message) {
    super(message);
  }

  /**
   * Instantiates a new invalid model exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public InvalidModelException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new invalid model exception.
   * 
   * @param cause
   *          the cause
   */
  public InvalidModelException(Throwable cause) {
    super(cause);
  }

}
