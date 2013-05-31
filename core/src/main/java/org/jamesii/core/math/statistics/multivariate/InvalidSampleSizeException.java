/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.multivariate;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class InvalidSampleSizeException extends RuntimeException {

  private static final long serialVersionUID = 9125388804525895114L;

  public InvalidSampleSizeException() {
  }

  public InvalidSampleSizeException(String message) {
    super(message);
  }

  public InvalidSampleSizeException(Throwable cause) {
    super(cause);
  }

  public InvalidSampleSizeException(String message, Throwable cause) {
    super(message, cause);
  }

}
