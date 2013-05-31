/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.storage;

public class InvalidDataStorageSetupException extends RuntimeException {

  private static final long serialVersionUID = 5486218437118361600L;

  public InvalidDataStorageSetupException() {
  }

  public InvalidDataStorageSetupException(String message) {
    super(message);
  }

  public InvalidDataStorageSetupException(Throwable cause) {
    super(cause);
  }

  public InvalidDataStorageSetupException(String message, Throwable cause) {
    super(message, cause);
  }

}
