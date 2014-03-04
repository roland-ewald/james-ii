/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.core.util.database.SimpleDataBaseEntity;

/**
 * Common superclass for all database entities with a name and a description.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public abstract class NamedDBEntity<X extends SimpleDataBaseEntity<X>> extends
    SimpleDataBaseEntity<X> {

  /** Name of the entity. */
  private String name = null;

  /** Description of the entity. */
  private String description = null;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
