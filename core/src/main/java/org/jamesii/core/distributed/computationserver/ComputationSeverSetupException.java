/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.computationserver;

/**
 * @author Jan Himmelspach
 * 
 */
public class ComputationSeverSetupException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 5099119810664803253L;

  /**
   * 
   */
  public ComputationSeverSetupException() {

  }

  /**
   * @param message
   */
  public ComputationSeverSetupException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public ComputationSeverSetupException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public ComputationSeverSetupException(String message, Throwable cause) {
    super(message, cause);
  }

}
