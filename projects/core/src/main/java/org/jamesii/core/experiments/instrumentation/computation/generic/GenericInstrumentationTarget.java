/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation.generic;

/**
 * Determines at which entity the {@link GenericComputationInstrumenter}
 * registers the {@link org.jamesii.core.observe.IObserver} that shall be used.
 * 
 * @author Roland Ewald
 */
public enum GenericInstrumentationTarget {

  /**
   * The state of a processor, as given via
   * {@link org.jamesii.core.processor.IProcessor#getgetState()}.
   */
  PROCESSOR_STATE,

  /** The {@link org.jamesii.core.processor.IProcessor} itself. */
  PROCESSOR,

  /**
   * The model processed by the processor, as given via
   * {@link org.jamesii.core.processor.IProcessor#getModel()}.
   */
  MODEL

}
