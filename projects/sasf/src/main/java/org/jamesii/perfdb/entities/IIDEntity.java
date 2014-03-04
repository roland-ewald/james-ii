/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import java.io.Serializable;

/**
 * Interface for all objects that have a database ID.
 * 
 * @author Roland Ewald
 * 
 */
public interface IIDEntity extends Serializable {

  /**
   * Gets the ID.
   * 
   * @return the ID
   */
  long getID();

  /**
   * Sets the ID.
   * 
   * @param id
   *          the new ID
   */
  void setID(long id);

}
