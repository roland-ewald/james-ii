/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

/**
 * This exception might be thrown from within the FileRowSet to stop if a
 * serious problem (like I/O access errors occur).
 * 
 * @author Jan Gimmelspach
 * 
 */
public class FileRowSetException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -2822803244769551953L;

  /**
   * 
   */
  public FileRowSetException() {
  }

  /**
   * @param message
   */
  public FileRowSetException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public FileRowSetException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public FileRowSetException(String message, Throwable cause) {
    super(message, cause);
  }

}
