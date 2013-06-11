/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation;

import java.util.List;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;

/**
 * Super interface of instrumenter interfaces. Instrumenters assign observers to
 * entities. Theses observers have to be made public, otherwise they cannot
 * propagate data to dependent components (e.g., visualisation components).
 * 
 * @author Roland Ewald
 * 
 */
public interface IInstrumenter {

  /**
   * Get all observers that were instantiated by this instrumenter. The returned
   * observers will be made public within the GUI, so that appropriate
   * visualisations can be assigned to them. This function will be called
   * *after* the instrumentation has happened.
   * 
   * @return list of observers
   */
  List<? extends IObserver<? extends IObservable>> getInstantiatedObservers();

}
