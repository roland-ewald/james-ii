/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.random;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory that creates a random predictor generator.
 * 
 * @author Roland Ewald
 * 
 */
public class RandomPredictorGeneratorFactory extends
    PerformancePredictorGeneratorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 639668335538612913L;

  @Override
  public IPerformancePredictorGenerator createPredictorGenerator(
      ParameterBlock params, PerformanceTuple example) {
    return new RandomPredictorGenerator();
  }

}
