/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote;

/**
 * This exception gets thrown by a {@link IMigrationController} when a migration
 * fails.
 * 
 * @author Simon Bartels
 * @author Roland Ewald
 */
public abstract class MigrationException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2380377413729002428L;

  /**
   * Instantiates a new Migration Exception.
   * 
   * @param message
   *          The error message.
   */
  public MigrationException(String message) {
    super(message);
  }

}
