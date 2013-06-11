/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

/**
 * This exception will be thrown if an object to be retrieved from a database is
 * not available.
 * 
 * @author Jan Himmelspach
 * 
 */
public class ObjectNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -8712066744068359476L;

  public ObjectNotFoundException() {
    super();
  }

  public ObjectNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ObjectNotFoundException(String message) {
    super(message);
  }

  public ObjectNotFoundException(Throwable cause) {
    super(cause);
  }

}
