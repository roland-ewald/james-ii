/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral;

import java.io.Serializable;

/**
 * The Interface IObjectID.
 * 
 * This interface is a "marker" interface. It has to be implemented by the
 * object to be used as identification for the objects in the "hostcentral"
 * communication schema.<br>
 * 
 * These can be simple string, unique ids, the model classes as such or whatever
 * else. But the author has to make sure that no two objects are identical in
 * regards to their ID if they are different objects.
 * 
 * @author Jan Himmelspach
 * @author Simon Bartels
 * 
 */
public interface IObjectId extends Serializable {

  /**
   * This method is necessary for the HostCentralModelFactory. It needs to know
   * which Object inherits this id.
   * 
   * @return Class of the Object with this ID.
   */
  String getClassNameFromObject();

  /**
   * Gets the string representation of the ID.
   * 
   * @return the string representation of this ID
   */
  String getStringRep();

}
