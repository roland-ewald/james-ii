/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model;

import java.net.URI;

import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;

/**
 * The Class ModelJDBCReaderWriterFactory. Abstract class for model reading /
 * writing from JDBC data sources. Checks whether an URI scheme is jdbc-X, with
 * X the value returned by the {@link #getType()} method.
 * 
 * @author Roland Ewald
 */
public abstract class ModelJDBCReaderFactory extends ModelReaderFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5269158886780788187L;

  /**
   * Gets the description.
   * 
   * @return the description
   */
  public abstract String getDescription();

  /**
   * Gets the type. Has to be the second part of an URI.
   * 
   * @return the type
   */
  public abstract String getType();

  @Override
  public boolean supportsURI(URI uri) {
    if (uri.getScheme().equals("jdbc-" + getType())) {
      return true;
    }
    return false;
  }

}
