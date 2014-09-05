/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.crossvalidation;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.plugintype.PredictorGeneratorEvaluationFactory;


/**
 * Factory for cross validation predictor generator evaluator.
 * 
 * @author Roland Ewald
 */
public class CrossValidationFactory extends PredictorGeneratorEvaluationFactory {

  /** The default number of folds. */
  private static final int DEFAULT_NUMBER_FOLDS = 10;

  /** Serialisation ID. */
  private static final long serialVersionUID = -6933977577562207877L;

  /** Name of fold parameter, type {@link Integer}. */
  public static final String FOLDS = "folds";

  /** Name of leave-one-out switch parameter, type {@link Boolean}. */
  public static final String LEAVE_ONE_OUT = "leaveOneOut";

  @Override
  public IPredictorGeneratorEvaluationStrategy create(ParameterBlock params, Context context) {
    return new CrossValidation(params.getSubBlockValue(FOLDS,
        DEFAULT_NUMBER_FOLDS), params.getSubBlockValue(LEAVE_ONE_OUT, false),
        params.getSubBlockValue(NUMBER_OF_PASSES, DEFAULT_VAL_NUM_PASSES));
  }

}
