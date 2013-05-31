/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables;

/**
 * This exception is thrown whenever the {@link ExperimentVariables} are
 * finished with generating parameter set-ups.
 * 
 * @author Jan Himmelspach
 * 
 */
public class NoNextVariableException extends RuntimeException {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -4724621905271601893L;

  public NoNextVariableException() {
    super();
  }

  public NoNextVariableException(String message) {
    super(message);
  }

  public NoNextVariableException(Throwable cause) {
    super(cause);
  }

  public NoNextVariableException(String message, Throwable cause) {
    super(message, cause);
  }

}
