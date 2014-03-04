/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

/**
 * An exception to signal that the creation of a
 * {@link org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator} failed.
 * 
 * @author Roland Ewald
 */
public class PredictorGenerationException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1517829574913600514L;

  /**
   * Instantiates a new predictor generator evaluation exception.
   */
  public PredictorGenerationException() {
    super();
  }

  /**
   * Instantiates a new predictor generator evaluation exception.
   * 
   * @param cause
   *          the cause of the exception
   */
  public PredictorGenerationException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new predictor generator evaluation exception.
   * 
   * @param msg
   *          the message
   */
  public PredictorGenerationException(String msg) {
    super(msg);
  }

}