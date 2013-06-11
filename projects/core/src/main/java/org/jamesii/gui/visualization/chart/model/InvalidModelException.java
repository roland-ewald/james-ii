/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model;

/**
 * @author Jan Himmelspach
 * 
 */
public class InvalidModelException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 7734218677379468551L;

  /**
   * 
   */
  public InvalidModelException() {

  }

  /**
   * @param message
   */
  public InvalidModelException(String message) {
    super(message);

  }

  /**
   * @param cause
   */
  public InvalidModelException(Throwable cause) {
    super(cause);

  }

  /**
   * @param message
   * @param cause
   */
  public InvalidModelException(String message, Throwable cause) {
    super(message, cause);

  }

}
