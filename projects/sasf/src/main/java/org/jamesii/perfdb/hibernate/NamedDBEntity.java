/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import org.jamesii.perfdb.entities.INamedDBEntity;

/**
 * Super class for all hibernate implementations that have a name, a
 * description, and an ID.
 * 
 * @author Roland Ewald
 * 
 */
public class NamedDBEntity extends IDEntity implements INamedDBEntity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6231461820226037043L;

  /** The name of the entity. */
  private String name = "";

  /** The ID of the entity. */
  private String description = "";

  /**
   * Empty constructor for beans compliance.
   */
  public NamedDBEntity() {
  }

  /**
   * Instantiates a new named db entity.
   * 
   * @param name
   *          the name
   * @param desc
   *          the description
   */
  public NamedDBEntity(String name, String desc) {
    this.name = name;
    this.description = desc;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setDescription(String desc) {
    description = desc;
  }

  @Override
  public void setName(String n) {
    name = n;
  }

}
