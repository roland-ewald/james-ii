/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.ensemble;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.asf.spdm.generators.random.RandomPredictorGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Creates an {@link EnsemblePredictorGenerator}.
 * 
 * @author Roland Ewald
 */
public class EnsemblePredictorGenFactory extends
    PerformancePredictorGeneratorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1427351939547936012L;

  /**
   * The predictor generator factory to be used. Type:
   * {@link PerformancePredictorGeneratorFactory}.
   */
  public static final String PREDICTOR_GEN_FACTORY = "predGenFactory";

  /**
   * The parameters for the predictor generator factory. Type:
   * {@link ParameterBlock}.
   */
  public static final String PREDICTOR_GEN_PARAMS = "predGenFacParams";

  @Override
  public IPerformancePredictorGenerator createPredictorGenerator(
      ParameterBlock params, PerformanceTuple example) {
    return new EnsemblePredictorGenerator(params.getSubBlockValue(
        PREDICTOR_GEN_FACTORY, new RandomPredictorGeneratorFactory()),
        params.getSubBlockValue(PREDICTOR_GEN_PARAMS, new ParameterBlock()));
  }

}
