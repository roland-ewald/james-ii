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
 * Factory for the {@link RandomFixedPredictorGenerator}.
 * 
 * @author Roland Ewald
 * 
 */
public class RandomFixedPredictorGeneratorFactory extends
    PerformancePredictorGeneratorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1796145563433279924L;

  @Override
  public IPerformancePredictorGenerator createPredictorGenerator(
      ParameterBlock params, PerformanceTuple example) {
    return new RandomFixedPredictorGenerator();
  }

}
