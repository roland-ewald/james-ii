/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

/**
 * This exception will be thrown if a computation task, which shall be started,
 * stopped, etc is unknown on the host it is intended to be.
 * 
 * @author Jan Himmelspach
 * 
 */
public class UnknownComputationTaskException extends RuntimeException {

  /**
   * The constant serial version UID
   */
  private static final long serialVersionUID = 3764296641423962266L;

  public UnknownComputationTaskException() {
  }

  public UnknownComputationTaskException(String message) {
    super(message);
  }

  public UnknownComputationTaskException(Throwable cause) {
    super(cause);
  }

  public UnknownComputationTaskException(String message, Throwable cause) {
    super(message, cause);
  }

}
