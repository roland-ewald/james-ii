/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.ports;

/**
 * The Class InvalidPortException.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class InvalidPortException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -5927565114838534683L;

  /** Creates a new instance of InvalidPortException */
  public InvalidPortException() {
    super();
  }

  /**
   * The Constructor.
   * 
   * @param message
   *          error message
   */
  public InvalidPortException(String message) {
    super(message);
  }
}
