/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class OutOfBoundsException extends RuntimeException {

  private static final long serialVersionUID = -1214722252870412754L;

  public OutOfBoundsException() {
  }

  public OutOfBoundsException(String message) {
    super(message);
  }

  public OutOfBoundsException(Throwable cause) {
    super(cause);
  }

  public OutOfBoundsException(String message, Throwable cause) {
    super(message, cause);
  }

}
