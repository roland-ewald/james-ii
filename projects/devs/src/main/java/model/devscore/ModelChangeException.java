/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;

/**
 * The Class ModelChangeException.
 * 
 * @author Jan Himmelspach
 */
public class ModelChangeException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -5529759318930979021L;

  /**
   * Creates a new instance of ModelChangeException.
   */
  public ModelChangeException() {
    super();
  }

  /**
   * The Constructor.
   * 
   * @param message
   *          errormessage
   */
  public ModelChangeException(String message) {
    super(message);
  }
}
