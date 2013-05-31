/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.wrapper.plugintype;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base factory to create data storage wrappers.
 * 
 * @author Jan Himmelspach
 */
public abstract class DataStorageWrapperFactory extends
    Factory<IDataStorage<?>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6049665888265443401L;

  /** The parameter ident for data storage */
  public static final String DATASTORAGE = "datastorage";

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

}
