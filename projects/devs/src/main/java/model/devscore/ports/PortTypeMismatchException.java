/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.ports;

/**
 * The Class PortTypeMismatchException.
 * 
 * @author Jan Himmelspach
 */
public class PortTypeMismatchException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -6243380822197920127L;

  /**
   * Constructor.
   */
  public PortTypeMismatchException() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param message
   *          which is piped out if this Exception occurs
   */
  public PortTypeMismatchException(String message) {
    super(message);
  }

}
