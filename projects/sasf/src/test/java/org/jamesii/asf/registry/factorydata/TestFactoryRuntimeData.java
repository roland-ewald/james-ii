/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.factorydata;

import org.jamesii.asf.portfolios.stochsearch.StochSearchPortfolioSelFactory;
import org.jamesii.core.plugins.metadata.FactoryRuntimeData;
import org.jamesii.core.util.misc.SimpleSerializationTest;


/**
 * The Class TestFactoryRuntimeData.
 * 
 * @author Roland Ewald
 */
public class TestFactoryRuntimeData extends
    SimpleSerializationTest<FactoryRuntimeData<StochSearchPortfolioSelFactory>> {

  @Override
  public void assertEquality(
      FactoryRuntimeData<StochSearchPortfolioSelFactory> original,
      FactoryRuntimeData<StochSearchPortfolioSelFactory> deserialisedVersion) {
    assertEquals(original.getFactory(), deserialisedVersion.getFactory());
  }

  @Override
  public FactoryRuntimeData<StochSearchPortfolioSelFactory> getTestObject()
      throws Exception {
    FactoryRuntimeData<StochSearchPortfolioSelFactory> frd = new FactoryRuntimeData<>(
        StochSearchPortfolioSelFactory.class);
    return frd;
  }

}