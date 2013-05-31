/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks;

/**
 * @author Jan Himmelspach
 * 
 */
public class InvalidComputationTaskConfiguration extends RuntimeException {

  private static final long serialVersionUID = 1572758542187520801L;

  public InvalidComputationTaskConfiguration() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   */
  public InvalidComputationTaskConfiguration(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public InvalidComputationTaskConfiguration(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public InvalidComputationTaskConfiguration(String message, Throwable cause) {
    super(message, cause);
  }

}
