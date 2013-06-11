/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic.convert;

/**
 * The Class ConversionException.
 * 
 * @author Jan Himmelspach
 */
public class ConversionException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7829815430290375814L;

  /**
   * Instantiates a new conversion exception.
   * 
   * @param msg
   *          the msg
   */
  public ConversionException(String msg) {
    super(msg);
  }

  /**
   * Instantiates a new conversion exception.
   * 
   * @param msg
   * @param t
   */
  public ConversionException(String msg, Throwable t) {
    super(msg, t);
  }

}
