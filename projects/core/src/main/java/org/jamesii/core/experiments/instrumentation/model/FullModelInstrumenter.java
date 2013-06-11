/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.model;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.base.INamedEntity;
import org.jamesii.core.data.runtime.IWriteReadDataStorage;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.instrumentation.AbstractFullInstrumenter;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;

/**
 * Fully instruments an {@link IModel}.
 * 
 * @author Jan Himmelspach
 */
public class FullModelInstrumenter extends AbstractFullInstrumenter implements
    IModelInstrumenter {

  /** The serialization ID. */
  private static final long serialVersionUID = 6311935596908784621L;

  /** The observer. */
  private List<IObserver<? extends IObservable>> observer = new ArrayList<>();

  @Override
  public void instrumentModel(IModel model,
      IComputationTaskConfiguration simConfig) { //
    DataObserver dobs = new DataObserver();

    observer.add(dobs);

    instrument(model);
  }

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    return observer;
  }

  @Override
  protected void instrumentObservable(IObservable observable) {
    observable.registerObserver(observer.get(0));
  }

  /**
   * An asynchronous update interface for receiving notifications about Data
   * information as the Data is constructed.
   */
  public static class DataObserver extends
      org.jamesii.core.observe.StorageObserver<IEntity, IWriteReadDataStorage> {

    @Override
    public void update(IEntity entity) {
      String name;
      if (entity instanceof INamedEntity) {
        name = ((INamedEntity) entity).getName();
      } else {
        name = entity.getClass().getName();
      }
      ((IWriteReadDataStorage) getDataStorage()).writeData(
          entity.getSimpleId(), name, -1, entity.toString());
    }

  }

}
