/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.problemsolver;

/**
 * This exception should be used whenever a {@link IProblemSolver} uses a wrong
 * {@link IProblemSolverState}.
 * 
 * @author Stefan Leye
 */
public class InvalidProblemSolverStateException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2100837481215386219L;

  /**
   * Instantiates a new invalid problem solver state exception.
   * 
   * @param message
   *          the message
   */
  public InvalidProblemSolverStateException(String message) {
    super(message);
  }

  /**
   * Instantiates a new invalid problem solver state exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public InvalidProblemSolverStateException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new invalid problem solver state exception.
   * 
   * @param cause
   *          the cause
   */
  public InvalidProblemSolverStateException(Throwable cause) {
    super(cause);
  }

}
