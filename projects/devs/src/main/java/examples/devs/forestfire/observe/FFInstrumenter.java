/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire.observe;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.model.AbstractState;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.Mediator;

import model.devs.IAtomicModel;
import model.devs.ICoupledModel;

/**
 * The Class CAStateInstrumenter.
 * 
 * @author Jan Himmelspach
 */
public class FFInstrumenter implements IModelInstrumenter {

  /** Serialization ID. */
  private static final long serialVersionUID = -3953679856426829557L;

  /** The observers. */
  private List<IObserver<? extends IObservable>> observers;

  @Override
  public void instrumentModel(IModel model,
      IComputationTaskConfiguration simConfig) {

    ICoupledModel m = (ICoupledModel) model;

    AbstractState mapState = ((IAtomicModel<?>) m.getModel("Map")).getState();

    mapState.setMediator(new Mediator());

    // System.out.println("Instrumenting the model");

    IObserver<? extends IObservable> obs = null;

    obs = new FFStateModelObs(model);

    if (obs == null) {
      return;
    }

    mapState.registerObserver(obs);
    observers = new ArrayList<>();
    // observers.clear();
    observers.add(obs);
  }

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    return observers;
  }
}
