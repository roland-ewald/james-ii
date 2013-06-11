/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.base.IEntity;

/**
 * The Class Mediator.
 * 
 * @author Jan Himmelspach
 */
public class Mediator implements IMediator, Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -7847827154668817494L;

  /** The objectives. */
  private Map<Object, List<IObserver<?>>> objectives = new HashMap<>();

  /** The updates. */
  private transient List<MediatorData> updates = new ArrayList<>();

  /**
   * By default return true, i.e. immediately inform the observers
   * 
   * @return true, if checks for to update
   */
  public boolean hasToUpdate() {
    return true;
  }

  @Override
  public synchronized void register(IObservable entity, IObserver<?> observer) {

    List<IObserver<?>> observers = objectives.get(entity);

    if (observers == null) {
      observers = new ArrayList<>(1);
      objectives.put(entity, observers);
    }

    observers.add(observer);

    /*
     * if (entity.getMediator() == null) { entity.setMediator (this); }
     */
  }

  @Override
  public synchronized void unRegister(IObservable entity) {
    objectives.remove(entity);
  }

  @Override
  public synchronized void unRegister(IObservable entity, IObserver<?> observer) {
    List<IObserver<?>> obsList = objectives.get(entity);
    if (obsList != null) {
      obsList.remove(observer);
    }
  }

  @Override
  public List<IObserver<?>> getObserver(IObservable entity) {
    return objectives.get(entity);
  }

  /**
   * Update.
   */
  @SuppressWarnings("unchecked")
  // casts to IObserver<IObservable> are OK because type compatibility is
  // ensured
  public final synchronized void update() {
    List<IObserver<?>> o;
    IObservable e;
    Object h;
    int j;
    for (int i = 0; i < updates.size(); i++) {
      e = updates.get(i).getEntity();
      h = updates.get(i).getHint();
      o = objectives.get(e);
      if (o == null) {
        continue;
      }
      if (h == null) {
        // if there is no hint
        for (j = 0; j < o.size(); j++) {
          ((IObserver<IObservable>) o.get(j)).update(e);
        }
      } else { // if there is a hint per update
        for (j = 0; j < o.size(); j++) {
          ((IObserver<IObservable>) o.get(j)).update(e, h);
        }
      }

    }
    updates.clear();
  }

  @Override
  public final synchronized void notifyObservers(IObservable sender) {
    updates.add(new MediatorData(sender));
    if (hasToUpdate()) {
      update();
    }
  }

  @Override
  public final synchronized void notifyObservers(IObservable sender, Object hint) {
    updates.add(new MediatorData(sender, hint));
    if (hasToUpdate()) {
      update();
    }
  }

  /**
   * Creates mediator if entity is not already mediated (in this case, nothing
   * happens).
   * 
   * @param entity
   *          entity for which the mediator shall be created and set
   */
  public static void create(IEntity entity) {
    if (entity.getMediator() == null) {
      entity.setMediator(new Mediator());
    }
  }

  /**
   * Is called from the JAVA serialization API. Added
   * {@link #setTransientFields()} call.
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    setTransientFields();
  }

  /**
   * Restores transient fields after serialisation.
   */
  private synchronized void setTransientFields() {
    updates = new ArrayList<>();
  }

}
