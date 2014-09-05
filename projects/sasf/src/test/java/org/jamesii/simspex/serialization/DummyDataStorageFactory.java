/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.serialization;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.util.JamesSimDataProvider;

/**
 * Dummy data storage factory for testing the serialization of
 * {@link JamesSimDataProvider}.
 * 
 * @author Roland Ewald
 * 
 */
public class DummyDataStorageFactory extends DataStorageFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -38269949553852002L;

  @Override
  public IDataStorage<?> create(ParameterBlock parameter, Context context) {
    return null;
  }

}