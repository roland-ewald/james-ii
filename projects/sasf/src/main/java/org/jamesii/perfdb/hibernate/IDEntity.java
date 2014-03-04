/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import org.jamesii.perfdb.entities.IIDEntity;

/**
 * Super class for all hibernate implementations having an ID field.
 * 
 * @author Roland Ewald
 * 
 */
public class IDEntity implements IIDEntity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4263779272602604641L;

  /** The ID of the entity. */
  private long id = -1;

  @Override
  public long getID() {
    return id;
  }

  @Override
  public void setID(long id) {
    this.id = id;
  }

}
