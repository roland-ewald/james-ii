/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import java.io.Serializable;

/**
 * Interface for objects that provide result data for a particular
 * {@link IApplication}, e.g. by looking it up from a database or by reading it
 * from a file.
 * 
 * @author Roland Ewald
 * 
 */
public interface IResultDataProvider<D> extends Serializable {

  /**
   * Gets the resulting data of the application.
   * 
   * @param parameters
   *          the parameters (to select the right data)
   * @return the data
   */
  D getData(Object... parameters);

  /**
   * Gets the type of the data.
   * 
   * @return the data type
   */
  Class<? extends D> getDataType();

}
