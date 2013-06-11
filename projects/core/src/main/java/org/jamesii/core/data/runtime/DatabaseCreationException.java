/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

/**
 * This exception will be thrown if the data base cannot be created.
 * 
 * @author Jan Himmelspach
 * 
 */
public class DatabaseCreationException extends RuntimeException {

  private static final long serialVersionUID = -8227344744369560955L;

  public DatabaseCreationException() {
  }

  public DatabaseCreationException(String message) {
    super(message);
  }

  public DatabaseCreationException(Throwable cause) {
    super(cause);
  }

  public DatabaseCreationException(String message, Throwable cause) {
    super(message, cause);
  }

}
