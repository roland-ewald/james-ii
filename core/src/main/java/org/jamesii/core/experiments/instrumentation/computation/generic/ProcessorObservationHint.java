/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation.generic;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;

/**
 * Hint that is created by the {@link GenericComputationInstrumenter} to notify
 * the observer that it has been registered at a new processor.
 * 
 * @see GenericComputationInstrumenter
 * @author Roland Ewald
 */
public class ProcessorObservationHint {

  /** The instrumenter that registered the observer at a new processor. */
  private final IComputationInstrumenter instrumenter;

  public ProcessorObservationHint(IComputationInstrumenter instrumenter) {
    this.instrumenter = instrumenter;
  }

  public IComputationInstrumenter getInstrumenter() {
    return instrumenter;
  }

}
