/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.exceptions;

import org.jamesii.core.remote.IMigrationController;
import org.jamesii.core.remote.MigrationException;

/**
 * 
 * This exception is thrown by {@link IMigrationController} when another
 * controller denies receiving objects.
 * 
 * @author Simon Bartels
 * 
 */
public class MigrationDeniedException extends MigrationException {

  /**
   * The serial version id.
   */
  private static final long serialVersionUID = -7004066289745079854L;

  /**
   * Instantiates a new MigrationDeniedException.
   */
  public MigrationDeniedException() {
    super("Target has denied the objects.");
  }

}
