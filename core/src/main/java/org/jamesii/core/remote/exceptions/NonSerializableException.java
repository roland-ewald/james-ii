/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.exceptions;

import org.jamesii.core.remote.MigrationException;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.controller.MigrationController;

/**
 * 
 * This exception is thrown by {@link MigrationController} when an object is not
 * serializable.
 * 
 * @author Simon Bartels
 * 
 */
public class NonSerializableException extends MigrationException {

  /**
   * The serial version id.
   */
  private static final long serialVersionUID = -8171769582816865252L;

  /** The object id. */
  private final IObjectId objectID;

  /** The object class. */
  private final Class<?> objectClass;

  /**
   * 
   * @param id
   *          id of the object that should have been migrated
   * 
   * @param objectClass
   *          the class of the object that should have been migrated
   */
  public NonSerializableException(IObjectId id, Class<?> objectClass) {
    super("Tried to migrate non Serializable Object!\n" + "ObjectId: "
        + id.getStringRep() + "\n" + "Class: " + objectClass.getCanonicalName());
    objectID = id;
    this.objectClass = objectClass;
  }

  public NonSerializableException(IObjectId id) {
    super("Tried to migrate non Serializable Object!\n" + "ObjectId: "
        + id.getStringRep() + "\n" + "Class: null");
    objectID = id;
    objectClass = null;
  }

  /**
   * Gets the object id.
   * 
   * @return the object id
   */
  public IObjectId getObjectID() {
    return objectID;
  }

  /**
   * Gets the object class.
   * 
   * @return the object class
   */
  public Class<?> getObjectClass() {
    return objectClass;
  }

}
