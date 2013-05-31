/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.security.auth;

/**
 * An exception indicating that authorization hasn't been granted.
 * 
 * @author Simon Bartels
 * 
 */
public class SecurityException extends Exception {

  /**
   * the serial version UID
   */
  private static final long serialVersionUID = 4603912082090016359L;

  public SecurityException() {
    super();
  }

  public SecurityException(String message, Throwable cause) {
    super(message, cause);
  }

  public SecurityException(String message) {
    super(message);
  }

  public SecurityException(Throwable cause) {
    super(cause);
  }
}
