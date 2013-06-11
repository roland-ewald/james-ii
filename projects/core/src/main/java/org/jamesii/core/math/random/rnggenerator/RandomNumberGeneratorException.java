/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.rnggenerator;

/**
 * @author Jan Himmelspach
 * 
 */
public class RandomNumberGeneratorException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1954750563231115673L;

  /**
   * 
   */
  public RandomNumberGeneratorException() {
  }

  /**
   * @param message
   */
  public RandomNumberGeneratorException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public RandomNumberGeneratorException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public RandomNumberGeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

}
