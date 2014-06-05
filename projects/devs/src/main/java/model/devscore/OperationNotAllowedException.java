/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;

/**
 * The Class OperationNotAllowedException.
 * 
 * @author Jan Himmelspach
 */
public class OperationNotAllowedException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4680802118157839213L;

  /**
   * Creates a new instance of OperationNotAllowedException.
   */
  public OperationNotAllowedException() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param message
   *          which describes the error message
   */
  public OperationNotAllowedException(String message) {
    super(message);
  }

}
