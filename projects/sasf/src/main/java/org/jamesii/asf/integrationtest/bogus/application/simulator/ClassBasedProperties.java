/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.simulator;


import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.util.misc.Strings;

/**
 * Simple class-based properties.
 * 
 * @author Roland Ewald
 */
public class ClassBasedProperties implements IBogusSimulatorProperties {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2119655415117645588L;

  /** The current factory for which the properties shall be extracted. */
  private Class<? extends FlexibleBogusSimulatorFactory> factory;

  /**
   * Instantiates a new class based properties.
   * 
   * @param factoryClass
   *          the factory class
   */
  public ClassBasedProperties(
      Class<? extends FlexibleBogusSimulatorFactory> factoryClass) {
    factory = factoryClass;
  }

  /**
   * Instantiates new class-based properties without setting any factory.
   */
  public ClassBasedProperties() {
  }

  @Override
  public int getLoadPerSteps(Map<String, Serializable> modelContent) {
    Serializable s = modelContent.get(Strings.dispClassName(factory));
    return s != null ? (Integer) s : DEFAULT;
  }

  public Class<? extends FlexibleBogusSimulatorFactory> getFactory() {
    return factory;
  }

  public void setFactory(Class<? extends FlexibleBogusSimulatorFactory> factory) {
    this.factory = factory;
  }

}
