/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation;

import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.crossvalidation.CrossValidation;

/**
 * Tests leave-one-out cross-validation.
 * 
 * @see CrossValidation
 * 
 * @author Roland Ewald
 * 
 */
public class LeaveOneOutCVTest extends PredictionGenEvaluationStrategyTest {

  @Override
  protected IPredictorGeneratorEvaluationStrategy getEvaluationStrategy() {
    return new CrossValidation(-1, true, 1);
  }

}
