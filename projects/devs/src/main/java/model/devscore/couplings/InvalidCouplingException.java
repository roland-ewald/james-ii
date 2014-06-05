/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

/**
 * The Class InvalidCouplingException.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class InvalidCouplingException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -4734472880430797217L;

  /**
   * Creates a new invalid coupling exception witht the given string as
   * exception message.
   * 
   * @param s
   *          error message
   */
  public InvalidCouplingException(String s) {
    super(s);
  }

}
