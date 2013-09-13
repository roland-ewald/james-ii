/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;


/**
 * This exception is thrown when a
 * {@link org.jamesii.core.experiments.taskrunner.ITaskRunner} encounters an
 * error when executing {@link TaskConfiguration} entities.
 * 
 * @author Jan Himmelspach
 * 
 */
public class ExperimentException extends RuntimeException {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -410780792966970124L;

  /**
   * Default constructor.
   * 
   * @param m
   *          the message
   */
  public ExperimentException(String m) {
    super(m);
  }

  /**
   * Instantiates a new experiment exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public ExperimentException(String message, Throwable cause) {
    super(message, cause);
  }

}
