/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

/**
 * This exception will be thrown if the setup of the computation contains a
 * problem.
 * 
 * @author Jan Himmelspach
 * 
 */
public class ComputationSetupException extends RuntimeException {

  private static final long serialVersionUID = -5060996737605227803L;

  public ComputationSetupException() {
    super();
  }

  public ComputationSetupException(String message, Throwable cause) {
    super(message, cause);
  }

  public ComputationSetupException(String message) {
    super(message);
  }

  public ComputationSetupException(Throwable cause) {
    super(cause);
  }

}
