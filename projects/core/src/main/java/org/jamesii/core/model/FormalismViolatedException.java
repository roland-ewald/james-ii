/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/*
 * Created on 12.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.jamesii.core.model;

/**
 * The Class FormalismViolatedException. This exception can and should be thrown
 * whenever a modelling formalism is violated.
 * 
 * @author Jan Himmelspach
 * 
 */
public class FormalismViolatedException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4227545216441048620L;

  /**
   * Instantiates a new formalism violated exception.
   */
  public FormalismViolatedException() {
    super();

  }

  /**
   * Instantiates a new formalism violated exception. The message should be used
   * to indicate how the formalism has been violated.
   * 
   * @param message
   *          the message
   */
  public FormalismViolatedException(String message) {
    super(message);

  }

  /**
   * Instantiates a new formalism violated exception. The message should be used
   * to indicate how the formalism has been violated.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public FormalismViolatedException(String message, Throwable cause) {
    super(message, cause);

  }

  /**
   * Instantiates a new formalism violated exception.
   * 
   * @param cause
   *          the cause
   */
  public FormalismViolatedException(Throwable cause) {
    super(cause);

  }

}
