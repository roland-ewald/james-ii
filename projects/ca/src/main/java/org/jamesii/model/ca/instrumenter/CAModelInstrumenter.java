/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.instrumenter;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.model.ca.observe.CAGridObserver;

/**
 * CAModelInstrumenter
 * 
 * Adds observers to a CA model.
 * 
 * Creation date: 18.10.07
 * 
 * @author Jan Himmelspach
 */
public class CAModelInstrumenter implements IModelInstrumenter {

  private static final long serialVersionUID = -7707403920039153087L;

  /**
   * Observer to be added to the model.
   */
  private IObserver<? extends IObservable> myObserver;

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    ArrayList<IObserver<? extends IObservable>> observers =
        new ArrayList<>();
    observers.add(myObserver);
    return observers;
  }

  /**
   * Instruments the given model.
   * 
   * @param model
   *          the model to instrument
   */
  @Override
  public void instrumentModel(IModel model,
      IComputationTaskConfiguration simConfig) {
    myObserver = new CAGridObserver();
    model.registerObserver(myObserver);
  }
}