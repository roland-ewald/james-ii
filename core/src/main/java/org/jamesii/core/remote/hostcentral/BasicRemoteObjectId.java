/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral;

/**
 * This is a simple ObjectId consisting of MAC, PID and hash code of a certain
 * object.
 * 
 * @author Simon Bartels
 * 
 */
public class BasicRemoteObjectId implements IObjectId {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5010895681183322564L;

  /**
   * Contains the real information.
   */
  private String id;

  /** The class from corresponding object. */
  private String classNameFromCorrespondingObject;

  @Override
  public String getClassNameFromObject() {
    return classNameFromCorrespondingObject;
  }

  /**
   * Instantiates a new basic remote object id.
   * 
   * @param id
   *          the id
   * @param classFromCorrespondingObject
   *          the class from corresponding object
   */
  public BasicRemoteObjectId(String id, String classNameFromCorrespondingObject) {
    this.id = id;
    this.classNameFromCorrespondingObject = classNameFromCorrespondingObject;
  }

  @Override
  public String toString() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BasicRemoteObjectId) {
      if (((BasicRemoteObjectId) o).getStringRep().equals(id)) {
        return true;
      }
    }
    // else
    return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Gets the id.
   * 
   * @return the id
   */
  @Override
  public String getStringRep() {
    return id;
  }

}
