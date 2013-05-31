/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation;

import java.util.List;

import org.jamesii.core.experiments.instrumentation.AbstractFullInstrumenter;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;

/**
 * Fully instruments a {@link IComputationTask}. TODO - this class is not
 * IMPLEMENTED SO FAR!!!
 * 
 * @author Jan Himmelspach
 */
public class FullComputationInstrumenter extends AbstractFullInstrumenter
    implements IComputationInstrumenter {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3627099074553781309L;

  @Override
  public void instrumentComputation(IComputationTask computationTask) {
    instrument(computationTask);
  }

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    return null;
  }

  @Override
  protected void instrumentObservable(IObservable observable) {
    throw new RuntimeException("Implemenation not completed.");
  }

}
