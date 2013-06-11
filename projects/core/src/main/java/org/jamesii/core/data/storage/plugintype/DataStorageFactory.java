/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.storage.plugintype;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory to create data storages.
 * 
 * @author Thomas Noesinger
 */
public abstract class DataStorageFactory extends Factory<IDataStorage<?>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7502556388955370366L;

  /** The s buffer size. */
  public static final String BUFFERSIZE = "bufferSize";

  /** The s experiment item. */
  public static final String EXPERIMENTITEM = "experimentItem";

  /**
   * Creates a new instance of DataStorageFactory.
   */
  public DataStorageFactory() {
  }

  /**
   * Creates a storage type with the given parameter.
   * 
   * @param parameter
   *          the parameters to be used to create the instance of the data
   *          storage. The parameters allowed here depend on the data storage to
   *          be created. An example for a such a parameter is the URL where the
   *          storage can be found.
   * 
   * @return an instance of a data storage accessible via the
   *         {@link org.jamesii.core.data.storage.IDataStorage} interface.
   */
  @Override
  public abstract IDataStorage<?> create(ParameterBlock parameter);

  /**
   * This function signalizes if the data storage factory at hand may be
   * initialized with a database server from the master server's management
   * component.
   * 
   * @return true if simulation runner shall query the master server to get
   *         access information for an eligible remote database (default is
   *         false)
   */
  public boolean usesMasterServer() {
    return false;
  }
}
