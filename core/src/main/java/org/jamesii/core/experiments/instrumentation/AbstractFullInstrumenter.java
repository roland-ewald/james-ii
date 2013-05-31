/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation;

import java.lang.reflect.Field;
import java.util.List;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;

/**
 * A full instrumenter instruments EVERYTHING (i.e., every observable property)
 * encapsulated in the passed entity. The entities to be instrumented are
 * determined by using Java reflection. You should not use this instrumenter if
 * only partial information is of interest - however it can be seen as fall-back
 * solution.
 * 
 * @author Jan Himmelspach
 */
public abstract class AbstractFullInstrumenter implements IInstrumenter {

  /**
   * Find observable entities and attach an observer to them. Makes use of Java
   * reflection to find the observable stuff. <br/>
   * Observable objects have to implement the
   * {@link org.jamesii.core.observe.IObservable} interface.
   * 
   * @param entity
   *          the entity
   */
  protected final void instrument(IObservable entity) {

    // attach observer to the passed entity
    instrumentObservable(entity);

    // examine the entity (whether it has sub entities to be observed on their
    // own)
    Class<?> c = entity.getClass();

    Field[] fs = c.getFields();

    try {
      for (Field f : fs) {
        // if we find an IEntity field we examine the field content as well
        if (f.getDeclaringClass().isAssignableFrom(IObservable.class)) {
          instrument((IObservable) f.get(entity));
        }
      }
    } catch (Exception e) {

    }
  }

  /**
   * Instrument the passed observable. This method is called from the
   * {@link #instrument(IObservable)} method. Here the observer has to be
   * attached, if required
   * 
   * @param observable
   *          the observable
   */
  protected abstract void instrumentObservable(IObservable observable);

  @Override
  public abstract List<? extends IObserver<? extends IObservable>> getInstantiatedObservers();

}
