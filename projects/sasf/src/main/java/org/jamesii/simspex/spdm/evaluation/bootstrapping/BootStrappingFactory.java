/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.bootstrapping;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.plugintype.PredictorGeneratorEvaluationFactory;


/**
 * Factory for bootstrapping.
 * 
 * @author Roland Ewald
 * 
 */
public class BootStrappingFactory extends PredictorGeneratorEvaluationFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3321156746274301406L;

  @Override
  public IPredictorGeneratorEvaluationStrategy create(
      ParameterBlock params) {
    return new BootStrapping(params.getSubBlockValue(NUMBER_OF_PASSES,
        DEFAULT_VAL_NUM_PASSES));
  }

}
