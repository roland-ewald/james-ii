/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.variables;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This class enhances the @see Environment with three features. Firstly, it
 * adds a method which returns an iterator of the internal value map. Secondly,
 * it adds two methods to merge this environment with another environment.
 * Thridly, it adds a method to copy an environment.
 * 
 * @author Tobias Helms
 * 
 * @param <K>
 */
public class MergableEnvironment<K extends Serializable> extends Environment<K>
    implements Iterable<Entry<K, Object>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6375553946326704397L;

  /**
   * Add all entries of the given environment to this environment and overwrite
   * existing values.
   */
  public void mergeWithOverwrite(MergableEnvironment<K> env) {
    for (Entry<K, Object> e : env) {
      setValue(e.getKey(), e.getValue());
    }
  }

  /**
   * Add all entries of the given environment to this environment and do not
   * overwrite all existing values.
   */
  public void mergeWithoutOverwrite(MergableEnvironment<K> env) {
    for (Entry<K, Object> e : env) {
      if (!containsIdent(e.getKey())) {
        setValue(e.getKey(), e.getValue());
      }
    }
  }

  /**
   * Return an iterator of the internal value structure.
   */
  public Iterator<Entry<K, Object>> iterator() {
    return getValues().entrySet().iterator();
  }

  /**
   * Copy this environment.
   */
  public MergableEnvironment<K> copy() {
    MergableEnvironment<K> result = new MergableEnvironment<>();
    for (Entry<K, Object> e : this) {
      result.setValue(e.getKey(), e.getValue());
    }
    return result;
  }
}
