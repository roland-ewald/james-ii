/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import org.jamesii.core.base.Entity;

/**
 * This delegate is intended for classes that do not inherit from {@link Entity}
 * for some reason (e.g. because they are not serializable) but still need to
 * implement {@link IObservable}.
 * 
 * 
 * 
 * TODO: This implementation is taken from {@link Entity}, it could be merged
 * with it but letting {@link Entity} use delegation could lead to a slow-down.
 * 
 * @author Roland Ewald
 */
public class ObservationDelegate implements IObservable {

  private IMediator mediator = null;

  private final IObservable observedObject;

  public ObservationDelegate(IObservable objectToManage) {
    observedObject = objectToManage;
  }

  @Override
  public void changed() {
    if (isObserved()) {
      mediator.notifyObservers(observedObject);
    }
  }

  @Override
  public void changed(Object hint) {
    if (isObserved()) {
      mediator.notifyObservers(this, hint);
    }
  }

  @Override
  public IMediator getMediator() {
    return mediator;
  }

  public final boolean isObserved() {
    return (mediator != null);
  }

  @Override
  public final void registerObserver(IObserver observer) {
    if (getMediator() == null) {
      throw new ObserverException(
          "You have to set a mediator before you can register an observer!");
    }
    this.getMediator().register(this, observer);
  }

  @Override
  public void setMediator(IMediator mediator) {
    this.mediator = mediator;
  }

  public final void unregister() {
    if (getMediator() != null) {
      this.getMediator().unRegister(this);
    }
  }

  @Override
  public final void unregisterObserver(IObserver observer) {
    this.getMediator().unRegister(this, observer);
  }

  @Override
  public void unregisterObservers() {
    getMediator().unRegister(this);
  }

}
