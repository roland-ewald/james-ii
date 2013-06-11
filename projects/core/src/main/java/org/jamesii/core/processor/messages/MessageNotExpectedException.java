/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.messages;

/**
 * The Class MessageNotExpectedException.
 */
public class MessageNotExpectedException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -9070198653764669187L;

  /**
   * Creates a new instance of MessageNotExpectedException.
   */
  public MessageNotExpectedException() {
    super();
  }

  /**
   * Creates a new instance of MessageNotExpectedException.
   * 
   * @param message
   *          the error message
   */
  public MessageNotExpectedException(String message) {
    super(message);
  }
}
