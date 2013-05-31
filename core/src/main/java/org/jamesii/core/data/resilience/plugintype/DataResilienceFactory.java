/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.resilience.plugintype;

import org.jamesii.core.data.resilience.IDataResilience;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for factories creating an {@link IDataResilience}.
 * 
 * @author Thomas Noesinger
 */
public abstract class DataResilienceFactory extends Factory<IDataResilience> {

  /** The serialization ID. */
  private static final long serialVersionUID = -1666422062716407561L;

  /** The key of database url. */
  public static final String DATABASEURL = "url";

  /** The key of driver. */
  public static final String DRIVER = "driver";

  /** The key of password. */
  public static final String PASSWORD = "pass";

  /** The key of user. */
  public static final String USER = "user";

  /**
   * Creates a new instance of DataStorageFactory.
   */
  public DataResilienceFactory() {
  }

  /**
   * Creates a new DataResilience object.
   * 
   * @param parameter
   *          the parameter block used to create the resilience
   * 
   * @return the data resilience
   */
  @Override
  public abstract IDataResilience create(ParameterBlock parameter);

}
