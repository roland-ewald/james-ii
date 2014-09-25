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
import org.jamesii.gui.visualization.grid.IGridCellRenderer;
import org.jamesii.model.carules.ICARulesModel;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.IGrid2D;

/**
 * The Class CAStateInstrumenter.
 * 
 * @author Jan Himmelspach
 */
public class CAStateInstrumenter implements IModelInstrumenter {

  /** Serialization ID. */
  private static final long serialVersionUID = -3953679856426829557L;

  /** The observers. */
  private List<IObserver<? extends IObservable>> observers;

  @Override
  public void instrumentModel(IModel model,
      IComputationTaskConfiguration simConfig) {
    model.setMediator(new Mediator());
    ICARulesModel m = (ICARulesModel) model;
    ICARulesGrid g = m.getGrid();

    // try to extract a custom cell renderer from the simulation configuration
    Object userParameters = simConfig.getParameters().get("user.parameters");
    IGridCellRenderer renderer = null;
    if (userParameters instanceof IGridCellRenderer) {
      renderer = (IGridCellRenderer) userParameters;
    }

    // System.out.println("Instrumenting the model");

    IObserver<? extends IObservable> obs = null;

    if (g.getSize().length == 1) {
      // create the observer to feed the grid "1d"
      obs = new CA1DStateModelObs(renderer);
    }

    if (g instanceof IGrid2D) {
      // create the observer to feed the grid 2d
      // obs = new org.jamesii.gui.visualization.grid.Grid2D();
      obs = new CAStateModelObs(renderer);
    }

    if (obs == null) {
      return;
    }

    model.registerObserver(obs);
    observers = new ArrayList<>();
    // observers.clear();
    observers.add(obs);
  }

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    return observers;
  }
}
