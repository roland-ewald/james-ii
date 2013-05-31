/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.exceptions;

/**
 * The {@link OperationNotSupportedException} is thrown if a method of class is
 * called (might be available because it has been defined in an interface the
 * class is implementing) to indicate that the called method is not supported by
 * the class.
 * 
 * This exception was added to confuse James II users who otherwise might be
 * confused by an {@link java.lang.UnsupportedOperationException} encountered
 * outside the "Java Collections Framework", as the latter's documentation
 * describes it to be "a member" thereof.
 * 
 * @author Jan Himmelspach
 * 
 */
public class OperationNotSupportedException extends RuntimeException {

  private static final long serialVersionUID = -996609121354059015L;

  public OperationNotSupportedException() {
  }

  public OperationNotSupportedException(String message) {
    super(message);
  }

  public OperationNotSupportedException(Throwable cause) {
    super(cause);
  }

  public OperationNotSupportedException(String message, Throwable cause) {
    super(message, cause);
  }

}
