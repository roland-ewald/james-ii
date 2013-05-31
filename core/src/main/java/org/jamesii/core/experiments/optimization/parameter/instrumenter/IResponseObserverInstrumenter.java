/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.instrumenter;

import java.util.List;
import java.util.Map;

import org.jamesii.core.experiments.instrumentation.IInstrumenter;
import org.jamesii.core.experiments.optimization.parameter.IResponseObserver;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;

/**
 * Interface for instrumenters used in the optimization context. These have to
 * return {@link IResponseObserver} objects instead of the usual
 * {@link IObserver} objects.
 * 
 * @author Roland Ewald
 * 
 */
public interface IResponseObserverInstrumenter extends IInstrumenter {

  @Override
  List<? extends IResponseObserver<? extends IObservable>> getInstantiatedObservers();

  /**
   * Retrieves/calculates the observed responses from all observers that have
   * been instantiated by this instrumenter.
   * 
   * @return a mapping name -> variable
   */
  Map<String, ? extends BaseVariable<?>> getObservedResponses();

}
