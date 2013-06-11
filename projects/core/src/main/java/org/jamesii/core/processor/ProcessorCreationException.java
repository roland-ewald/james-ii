/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

/**
 * The Class ProcessorCreationException.
 * 
 * @author Jan Himmmelspach
 */
public class ProcessorCreationException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -7962604235899419279L;

  /**
   * Creates a new instance of ProcessorCreationException.
   */
  public ProcessorCreationException() {
    super();
  }

  /**
   * Creates a new instance of ProcessorCreationException.
   * 
   * @param message
   *          errormessage
   */
  public ProcessorCreationException(String message) {
    super(message);
  }
}
