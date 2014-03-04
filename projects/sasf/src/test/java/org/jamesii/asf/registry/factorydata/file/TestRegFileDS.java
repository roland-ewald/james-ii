/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.factorydata.file;

import org.jamesii.asf.registry.factorydata.TestFactoryRuntimeDataStorage;
import org.jamesii.core.plugins.metadata.IFactoryRuntimeDataStorage;
import org.jamesii.core.plugins.metadata.file.RegFileDStorage;


/**
 * Tests for {@link RegFileDStorage}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestRegFileDS extends TestFactoryRuntimeDataStorage {

  @Override
  public IFactoryRuntimeDataStorage getFactoryRuntimeDataStorage()
      throws Exception {
    return new RegFileDStorage(".testregdata");
  }

}
