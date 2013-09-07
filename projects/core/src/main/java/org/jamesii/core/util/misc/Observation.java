/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.experiments.optimization.parameter.IResponseObserver;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.observe.IObservable;

/**
 * Simple utility class for observation.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 * 
 */
public final class Observation {

  /**
   * Hidden constructor.
   */
  private Observation() {
  }

  /**
   * Updates object recursively via reflection.
   * 
   * @param entity
   *          the entity
   */
  public static void updateObject(Object entity) {

    // update the passed entity
    if (entity instanceof IObservable) {
      ((IObservable) entity).changed();
    }

    // maybe better to look for getters???

    if (entity instanceof Iterable<?>) {
      for (Object o : (Iterable<?>) entity) {
        updateObject(o);
      }
    } else {
      // examine the entity (whether it has sub objects to be updated on their
      // own)
      Class<?> c = entity.getClass();

      Field[] fs = c.getFields();

      try {
        for (Field f : fs) {
          updateObject(f.get(entity));

        }
      } catch (Exception e) {
      }
    }
  }

  /**
   * Compile responses from {@link IResponseObserver}, eases implementation of
   * {@link org.jamesii.core.experiments.optimization.parameter.instrumenter.IResponseObserverInstrumenter}
   * .
   * 
   * @param observers
   *          the response observers
   * 
   * @return the response map
   */
  public static Map<String, BaseVariable<?>> compileResponses(
      List<IResponseObserver<? extends IObservable>> observers) {
    Map<String, BaseVariable<?>> map = new HashMap<>();
    for (IResponseObserver<? extends IObservable> obs : observers) {
      map.putAll(obs.getResponseList());
    }
    return map;
  }

}
