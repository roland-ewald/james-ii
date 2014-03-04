/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.simulator;


import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * Properties are defined by a simple integer parameter.
 * 
 * @author Roland Ewald
 * 
 */
public class ParameterBasedProperties extends ClassBasedProperties {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2685199588571021111L;

  /**
   * The name of the sub block in the parameter block from which the additional
   * load shall be extracted.
   */
  public static final String PARAMETER_NAME = "LoadPerStep";

  /** The load per steps. */
  private int additionalLoadPerSteps;

  /**
   * Instantiates a new parameter based properties.
   * 
   * @param factoryClass
   *          the factory class
   * @param paramBlock
   *          the param block
   */
  public ParameterBasedProperties(
      Class<? extends FlexibleBogusSimulatorFactory> factoryClass,
      ParameterBlock paramBlock) {
    super(factoryClass);
    additionalLoadPerSteps =
        paramBlock.getSubBlockValue(PARAMETER_NAME, DEFAULT);
  }

  @Override
  public int getLoadPerSteps(Map<String, Serializable> modelContent) {
    return super.getLoadPerSteps(modelContent) + additionalLoadPerSteps;
  }

  /**
   * Gets the additional load per steps.
   * 
   * @return the additional load per steps
   */
  public int getAdditionalLoadPerSteps() {
    return additionalLoadPerSteps;
  }

  /**
   * Sets the additional load per steps.
   * 
   * @param additionalLoadPerSteps
   *          the new additional load per steps
   */
  public void setAdditionalLoadPerSteps(int additionalLoadPerSteps) {
    this.additionalLoadPerSteps = additionalLoadPerSteps;
  }

}
