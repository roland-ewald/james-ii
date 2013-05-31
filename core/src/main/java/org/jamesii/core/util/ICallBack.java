/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

/**
 * Generic interface for call-back methods, to be called with a single generic
 * parameter.
 * 
 * @author Jan Himmelspach
 * 
 * @param <N>
 *          class of parameter to be handed over.
 */
public interface ICallBack<N> {

  /**
   * Process given parameter.
   * 
   * @param parameter
   *          the parameter
   * @return true if the parameter was handled, false otherwise
   */
  boolean process(N parameter);

}
