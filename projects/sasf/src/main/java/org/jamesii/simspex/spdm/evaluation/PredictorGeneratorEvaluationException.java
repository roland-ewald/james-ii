/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation;

/**
 * Exception to be thrown in case the evaluation of a
 * {@link org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator} fails.
 * 
 * @author Roland Ewald
 * 
 */
public class PredictorGeneratorEvaluationException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2251685425715218780L;

  /**
   * Instantiates a new predictor generator evaluation exception.
   */
  public PredictorGeneratorEvaluationException() {
  }

  /**
   * Instantiates a new predictor generator evaluation exception.
   * 
   * @param cause
   *          the cause of the exception
   */
  public PredictorGeneratorEvaluationException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new predictor generator evaluation exception.
   * 
   * @param msg
   *          the message
   */
  public PredictorGeneratorEvaluationException(String msg) {
    super(msg);
  }

}
