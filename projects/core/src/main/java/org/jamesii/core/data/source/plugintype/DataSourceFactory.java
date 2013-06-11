/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.source.plugintype;

import org.jamesii.core.data.source.IDataSource;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for creating data sources.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class DataSourceFactory extends Factory<IDataSource> {

  /** The serialization ID. */
  private static final long serialVersionUID = 7502556388955370366L;

  /**
   * Creates a new instance of DataStorageFactory.
   */
  public DataSourceFactory() {
  }

  /**
   * Creates a storage type with the given params.
   * 
   * @param params
   *          the params
   * 
   * @return the data source
   */
  @Override
  public abstract IDataSource create(ParameterBlock params);
}
