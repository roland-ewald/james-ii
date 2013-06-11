/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.execontrol;

/**
 * @author Jan Himmelspach
 * 
 */
public class InvalidExecutionStateException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 2082718656417094321L;

  /**
   * 
   */
  public InvalidExecutionStateException() {

  }

  /**
   * @param message
   */
  public InvalidExecutionStateException(String message) {
    super(message);

  }

  /**
   * @param cause
   */
  public InvalidExecutionStateException(Throwable cause) {
    super(cause);

  }

  /**
   * @param message
   * @param cause
   */
  public InvalidExecutionStateException(String message, Throwable cause) {
    super(message, cause);

  }

}
