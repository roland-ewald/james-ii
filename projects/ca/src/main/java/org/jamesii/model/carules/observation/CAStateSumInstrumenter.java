/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.observation;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.Mediator;

/**
 * The Class CAStateSumInstrumenter.
 * 
 * @author Roland Ewald
 */
public class CAStateSumInstrumenter implements IModelInstrumenter {

  /** Serialization ID. */
  private static final long serialVersionUID = -3953679856426829557L;

  /** The observers. */
  private List<IObserver<? extends IObservable>> observers;

  @Override
  public void instrumentModel(IModel model,
      IComputationTaskConfiguration simConfig) {
    model.setMediator(new Mediator());
    IObserver<? extends IObservable> obs = new CAStateSumModelObs();
    model.registerObserver(obs);
    observers = new ArrayList<>();
    observers.clear();
    observers.add(obs);
  }

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    return observers;
  }
}
