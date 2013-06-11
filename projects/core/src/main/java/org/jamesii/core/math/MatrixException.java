/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

/**
 * The Class MatrixException. Use by the {@link Matrix} class if a problem
 * occurs (e.g., an invalid matrix operation)
 * 
 * @author Jan Himmelspach
 */
public class MatrixException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4190330875712581557L;

  /**
   * Instantiates a new matrix exception.
   */
  public MatrixException() {
    super();
  }

  /**
   * The Constructor.
   * 
   * @param message
   *          the message
   */
  public MatrixException(String message) {
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
  public MatrixException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * The Constructor.
   * 
   * @param cause
   *          the cause
   */
  public MatrixException(Throwable cause) {
    super(cause);
  }

}
