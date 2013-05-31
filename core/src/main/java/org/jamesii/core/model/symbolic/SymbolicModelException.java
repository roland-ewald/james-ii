/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic;

/**
 * Exception which might be thrown by implementations of the
 * {@link ISymbolicModel} interface to indicate that a serious error happened
 * (e.g., conversion of a model failed).
 * 
 * @author Jan Himmelspach
 * 
 */
public class SymbolicModelException extends RuntimeException {

  /**
   * The constant serial version uid.
   */
  private static final long serialVersionUID = -2692318255959617972L;

  public SymbolicModelException() {
  }

  public SymbolicModelException(String message) {
    super(message);
  }

  public SymbolicModelException(Throwable cause) {
    super(cause);
  }

  public SymbolicModelException(String message, Throwable cause) {
    super(message, cause);
  }

}
