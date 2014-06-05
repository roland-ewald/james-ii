/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;

/**
 * The Class NotUniqueException.
 * 
 * @author Jan Himmelspach
 * 
 *         Created on 29. September 2003, 15:33
 */
public class NotUniqueException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -6330361286457006894L;

  /**
   * Creates a new instance of NotUniqueException.
   */
  public NotUniqueException() {
    super();
  }

  /**
   * Creates a new instance of NotUniqueException.
   * 
   * @param message
   *          the message
   */
  public NotUniqueException(String message) {
    super(message);
  }

}
