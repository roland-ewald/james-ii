/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

// TODO: Auto-generated Javadoc
/**
 * The Class InvalidChangeRequestException.
 * 
 * @author Jan Himmelspach
 */
public class InvalidChangeRequestException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -2194253690122590207L;

  /**
   * Instantiates a new invalid change request exception.
   */
  public InvalidChangeRequestException() {
    super();

  }

  /**
   * The Constructor.
   * 
   * @param message
   *          the message
   */
  public InvalidChangeRequestException(String message) {
    super(message);

  }

  /**
   * The Constructor.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public InvalidChangeRequestException(String message, Throwable cause) {
    super(message, cause);

  }

  /**
   * The Constructor.
   * 
   * @param cause
   *          the cause
   */
  public InvalidChangeRequestException(Throwable cause) {
    super(cause);

  }

}
