/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.mlj;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for Naive Bayes predictor generator.
 * 
 * @author Roland Ewald
 * 
 */
public class NaiveBayesPredictorGeneratorFactory extends
    PerformancePredictorGeneratorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3944025727214348520L;

  @Override
  public IPerformancePredictorGenerator createPredictorGenerator(
      ParameterBlock params, PerformanceTuple example) {
    return new NaiveBayesPredictorGenerator(params.getSubBlockValue(
        NUM_OF_CLASSES, DEFAULT_VAL_NUM_CLASSES));
  }

}
