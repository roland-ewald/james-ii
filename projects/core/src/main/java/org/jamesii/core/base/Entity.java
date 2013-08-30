/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.base;

import java.io.Serializable;

import org.jamesii.SimSystem;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.ObserverException;

/**
 * Entity is the base class of nearly all entities in the modeling and
 * simulation framework. An entity is observable, and provides an unique
 * identifier for the unique identification of an entity during a run of the
 * framework. Additionally entity provides the basic support for multiple
 * languages.
 * 
 * The few legacy report methods should not be used anymore. Reporting should be
 * done via {@link SimSystem}.
 * 
 * @author Jan Himmelspach
 */
public class Entity implements IEntity, Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4777595551925059508L;

  /** The mediator. */
  private IMediator mediator = null;

  /**
   * Unique ID for each object in the simulation system.
   */
  private long uid = 0;

  /**
   * Default constructor. Auto assigns a unique id to the entity.
   */
  public Entity() {
    super();
    uid = SimSystem.getUId();
  }

  @Override
  public void changed() {
    if (isObserved()) {
      mediator.notifyObservers(this);
    }
  }

  /**
   * Whenever changed is called any attached observer gets updated. This method
   * additionally propagates a "hint" which can contain information about the
   * change. If you do not need this you can simply use the {@link #changed()}
   * method instead.
   * 
   * @param hint
   *          the hint passed together with the change notification.
   */
  @Override
  public void changed(Object hint) {
    if (isObserved()) {
      mediator.notifyObservers(this, hint);
    }
  }

  @Override
  public String getCompleteInfoString() {
    return "";
  }

  @Override
  public IMediator getMediator() {
    return mediator;
  }

  @Override
  public long getSimpleId() {
    return uid;
  }

  /**
   * Checks if is observed.
   * 
   * @return true, if is observed (thus if an mediator is connected)
   */
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

  /**
   * Unregister this object at the {@link #getMediator()}. If no mediator is
   * present this method will do nothing.
   * 
   */
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
