/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

/**
 * Common interface for all database entities with a name and a description.
 * 
 * @author Roland Ewald
 * 
 */
public interface INamedDBEntity extends IIDEntity {

  /**
   * Gets the name of the entity.
   * 
   * @return the name
   */
  String getName();

  /**
   * Sets the name of the entity.
   * 
   * @param name
   *          the new name
   */
  void setName(String name);

  /**
   * Gets the description of the entity.
   * 
   * @return the description
   */
  String getDescription();

  /**
   * Sets the description of the entity.
   * 
   * @param description
   *          the new description
   */
  void setDescription(String description);

}
