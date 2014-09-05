/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.simple;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.plugintype.PredictorGeneratorEvaluationFactory;


/**
 * Factory for simple evaluation strategy (one pass with a given percentage as
 * training data and the rest as test data).
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleEvaluationFactory extends PredictorGeneratorEvaluationFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3443373656110363079L;

  /**
   * Name for parameterising training data percentage, type {@link Double},
   * needs to be in [0,1]. Default is
   * {@link SimpleEvaluationFactory#DEFAULT_VAL_TRAINING_DATA_PERCENTAGE}.
   */
  public static final String PERCENTAGE = "trainingDataPercentage";

  /** The default value for the percentage of data to be used for training. */
  private static final double DEFAULT_VAL_TRAINING_DATA_PERCENTAGE = 0.5;

  @Override
  public IPredictorGeneratorEvaluationStrategy create(
      ParameterBlock params, Context context) {
    return new SimpleEvaluationStrategy(params.getSubBlockValue(PERCENTAGE,
        DEFAULT_VAL_TRAINING_DATA_PERCENTAGE));
  }

}
