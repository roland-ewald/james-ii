/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation;

import java.io.Serializable;

import org.jamesii.core.experiments.instrumentation.IInstrumenter;
import org.jamesii.core.experiments.tasks.IComputationTask;

/**
 * Computation instrumenters add observers to the computation infrastructure.
 * E.g., to computation algorithms.
 * 
 * @author Roland Ewald Date: 15.06.2007
 */
public interface IComputationInstrumenter extends IInstrumenter, Serializable {

  /**
   * Instruments a computation. The instrumenter will add observers to parts of
   * the computation passed as parameter. What will be observed depends on the
   * instrumenter.
   * 
   * @param computation
   *          computation to be instrumented
   */
  void instrumentComputation(IComputationTask computation);

}
