/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

/**
 * The Class ReflectionException.
 * 
 * Used by the {@link org.jamesii.core.util.Reflect} class.
 * 
 * @author Jan Himmelspach
 */
public class ReflectionException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4874102801148648803L;

  /**
   * Instantiates a new reflection exception.
   * 
   * @param msg
   *          the msg
   */
  public ReflectionException(String msg) {
    super(msg);
  }

  /**
   * Instantiates a new reflection exception.
   * 
   * @param msg
   *          the msg
   * @param cause
   *          the cause
   */
  public ReflectionException(String msg, Throwable cause) {
    super(msg, cause);
  }

  /**
   * 
   * @param cause
   */
  public ReflectionException(Throwable cause) {
    super(cause);
  }

}
