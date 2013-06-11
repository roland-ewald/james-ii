/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.plugintype;

import org.jamesii.core.factories.AbstractFactory;

/**
 * Abstract class to choose a factory creating random number generators.
 * 
 * @author Jan Himmelspach
 */
public class AbstractRandomGeneratorFactory extends
    AbstractFactory<RandomGeneratorFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2522394361920608489L;

  /** Seeds of the RNG to be used. */
  public static final String SEED = "seed";

}
