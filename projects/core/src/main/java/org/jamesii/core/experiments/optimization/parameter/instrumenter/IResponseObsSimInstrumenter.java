/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.instrumenter;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;

/**
 * Interface for simulation instrumenters used in the optimization context.
 * These have to return
 * {@link org.jamesii.core.experiments.optimization.parameter.IResponseObserver}
 * objects instead of the usual {@link org.jamesii.core.observe.IObserver}
 * objects.
 * 
 * @author Roland Ewald
 * 
 */
public interface IResponseObsSimInstrumenter extends IComputationInstrumenter,
    IResponseObserverInstrumenter {

}
