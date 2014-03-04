/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.random;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.core.math.random.generators.IRandom;


/**
 * Random performance predictor (for testing purposes).
 * 
 * @author Roland Ewald
 * 
 */
public class RandomPredictor implements IPerformancePredictor {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4453123182755607523L;

  /** The random number generator to be used. */
  private transient IRandom random;

  /**
   * Instantiates a new random performance predictor.
   */
  public RandomPredictor() {
    random = SimSystem.getRNGGenerator().getNextRNG();
  }

  @Override
  public double predictPerformance(Features features, Configuration config) {
    // Necessary for serialization to work properly (constructor is not
    // called)
    if (random == null) {
      random = SimSystem.getRNGGenerator().getNextRNG();
    }
    return random.nextDouble();
  }
}