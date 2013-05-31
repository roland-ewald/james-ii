/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

/**
 * The invalid processor exception can be thrown if the processor to be used is
 * invalid.
 * 
 * @author Christian Ober
 * @version 1.0
 */
public class InvalidProcessorException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -5529759318930979021L;

  /** Creates a new instance of InvalidProcessorException */
  public InvalidProcessorException() {
    super();
  }

  /**
   * Creates a new instance of InvalidProcessorException containing more
   * information about the problem with the processor.
   * 
   * @param message
   *          the error message contained in the exception.
   */
  public InvalidProcessorException(String message) {
    super(message);
  }
}
