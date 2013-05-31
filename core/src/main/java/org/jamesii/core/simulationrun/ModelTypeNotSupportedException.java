/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun;

/**
 * The Class ModelTypeNotSupportedException.
 * 
 * @author Jan Himmelspach
 */
public class ModelTypeNotSupportedException extends RuntimeException {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -6580817662983969653L;

  /**
   * Creates a new instance of ModelTypeNotSupportedException.
   */
  public ModelTypeNotSupportedException() {
  }

  /**
   * Instantiates a new model type not supported exception.
   * 
   * @param msg
   *          the msg
   */
  public ModelTypeNotSupportedException(String msg) {
    super(msg);
  }
}
