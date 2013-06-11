/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.exceptions;

import org.jamesii.core.remote.IMigrationController;
import org.jamesii.core.remote.MigrationException;
import org.jamesii.core.remote.hostcentral.IObjectId;

/**
 * This exception is thrown by a {@link IMigrationController} when someone
 * triggers the migration of an object that's not local.
 * 
 * @author Simon Bartels
 * 
 */
public class NoLocalObjectException extends MigrationException {

  /**
   * The serial version id.
   */
  private static final long serialVersionUID = 4078841951296521526L;

  /**
   * The id of the object which is not local.
   */
  private final IObjectId objectID;

  /**
   * Instantiates a new NoLocalObjectException
   * 
   * @param id
   *          The id of the object which is not local.
   */
  public NoLocalObjectException(IObjectId id) {
    super("Object with id " + id.getStringRep() + " is not on this host");
    objectID = id;
  }

  /**
   * 
   * @return The id of the object which is not local.
   */
  public IObjectId getObjectID() {
    return objectID;
  }

}
