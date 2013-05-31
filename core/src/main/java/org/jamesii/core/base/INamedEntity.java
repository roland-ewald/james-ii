/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.base;

/**
 * Interface which extends the {@link IEntity} interface by means of naming.
 * This interface is implemented by the {@link NamedEntity} class.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public interface INamedEntity extends IEntity, Comparable<INamedEntity> {

  /**
   * Gets the name of the entity.
   * 
   * @return the name
   */
  String getName();

  /**
   * Sets the name of the entity. The name can be any valid string.
   * 
   * @param name
   *          the new name
   */
  void setName(String name);
}
