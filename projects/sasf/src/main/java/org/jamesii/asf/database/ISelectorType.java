/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database;

import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.perfdb.entities.INamedDBEntity;


/**
 * Represents a selector type in the database.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public interface ISelectorType extends INamedDBEntity {

  /**
   * Gets the selector generator factory.
   * 
   * @return the selector generator factory
   */
  Class<? extends PerformancePredictorGeneratorFactory> getSelectorGeneratorFactory();

  /**
   * Sets the selector generator factory.
   * 
   * @param selectorGeneratorFactory
   *          the new selector generator factory
   */
  void setSelectorGeneratorFactory(
      Class<? extends PerformancePredictorGeneratorFactory> selectorGeneratorFactory);

}
