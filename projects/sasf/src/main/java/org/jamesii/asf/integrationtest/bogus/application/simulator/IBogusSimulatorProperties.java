/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.simulator;

import java.io.Serializable;
import java.util.Map;

/**
 * Implementations determine the runtime characteristics of a
 * {@link FlexibleBogusSimulator}, depending on the properties of the
 * {@link org.jamesii.asf.integrationtest.bogus.application.model.IBogusModel} instance it has
 * to 'simulate'.
 * 
 * @author Roland Ewald
 */
public interface IBogusSimulatorProperties extends Serializable {

  /** The default load to be used in case the property is not set properly. */
  Integer DEFAULT = 1;

  /**
   * Gets the load per steps.
   * 
   * @param modelContent
   *          the model content
   * 
   * @return the load per steps
   */
  int getLoadPerSteps(Map<String, Serializable> modelContent);
}
