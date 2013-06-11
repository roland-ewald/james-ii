/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote;

/**
 * The Class RemoteCallForbidden.
 * 
 * This exception can be used to report that a method cannot be called on a
 * proxy. This might be due to the return value of the method being called: it
 * might only be valid on the remote site and should by no way be transferred to
 * the callers site.
 * 
 * @author Jan Himmelspach
 */
public class RemoteCallForbiddenException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 41472161415858804L;

  /**
   * Instantiates a new remote call forbidden.
   * 
   * @param message
   *          the message
   */
  public RemoteCallForbiddenException(String message) {
    super(message);
  }

}
