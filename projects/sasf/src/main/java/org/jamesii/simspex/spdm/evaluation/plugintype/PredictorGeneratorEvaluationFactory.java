/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;


/**
 * Base class for all predictor generation evaluation methods.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PredictorGeneratorEvaluationFactory extends
    Factory<IPredictorGeneratorEvaluationStrategy> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -272385689384842137L;

  /**
   * Parameter for number of repetitions, type: {@link Integer}. Default is
   * {@link PredictorGeneratorEvaluationFactory#DEFAULT_VAL_NUM_PASSES}.
   */
  public static final String NUMBER_OF_PASSES = "numberOfPasses";

  /**
   * The default value for the number of repetitions/passes of this evaluation
   * (in case it contains stochastic elements and supports this parameter).
   */
  public static final int DEFAULT_VAL_NUM_PASSES = 10;

  /**
   * Creates evaluation strategy.
   * 
   * @param params
   *          parameters for evaluation strategy
   * 
   * @return evaluation strategy
   */
  public abstract IPredictorGeneratorEvaluationStrategy create(
      ParameterBlock params);

}
