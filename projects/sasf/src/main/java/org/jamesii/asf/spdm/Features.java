/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm;

import java.io.Serializable;
import java.util.TreeMap;

import org.jamesii.core.util.exceptions.OperationNotSupportedException;
import org.jamesii.core.util.misc.Comparators;

/**
 * Represents features of a given simulation problem.
 * 
 * Extends {@link TreeMap} (instead of {@link java.util.HashMap}) to ensure an
 * alphabetical (and deterministic) ordering of features.
 * 
 * @author Roland Ewald
 * 
 */
public class Features extends TreeMap<String, Serializable> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4909295314013535453L;

  /*
   * Method overridden, to ensure that features are not put twice in the map
   * (exception is thrown in that case).
   */
  @Override
  public Serializable put(String key, Serializable value) {
    if (!this.containsKey(key)) {
      return super.put(key, value);
    } else {
      Serializable oldValue = super.get(key);
      if (Comparators.equal(oldValue, value)) {
        throw new IllegalArgumentException(
            "Tried to put existing feature into feature map! Existing feature: "
                + key + " = " + get(key) + "\n\n new value for feature: "
                + value);
      }
      return value;
    }
  }

  @Override
  public Serializable remove(Object key) {
    return new OperationNotSupportedException();
  }

}
