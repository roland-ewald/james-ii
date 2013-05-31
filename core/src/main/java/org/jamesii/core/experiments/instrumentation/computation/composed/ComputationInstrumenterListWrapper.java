/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation.composed;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;

/**
 * Wraps a list of instrumenters.
 * 
 * @author Roland Ewald
 * 
 */
public class ComputationInstrumenterListWrapper implements
    IComputationInstrumenter {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3460993671918672207L;

  /** The list of observers. */
  private final transient List<IObserver<? extends IObservable>> observers =
      new ArrayList<>();

  /** The list of instrumenters. */
  private final transient List<IComputationInstrumenter> instrumenters;

  /**
   * Instantiates a new computation instrumenter list.
   * 
   * @param instrList
   *          the list of instrumenters
   */
  public ComputationInstrumenterListWrapper(
      List<IComputationInstrumenter> instrList) {
    instrumenters = instrList;
  }

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    return observers;
  }

  @Override
  public void instrumentComputation(IComputationTask compTask) {
    for (IComputationInstrumenter instrumenter : instrumenters) {
      instrumenter.instrumentComputation(compTask);
      observers.addAll(instrumenter.getInstantiatedObservers());
    }
  }

}
