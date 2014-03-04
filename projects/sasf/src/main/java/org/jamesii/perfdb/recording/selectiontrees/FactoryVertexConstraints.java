/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.jamesii.core.factories.Factory;

/**
 * Instances of this class represent constraints for a given
 * {@link FactoryVertex}.
 * 
 * @author Roland Ewald
 * 
 */
public class FactoryVertexConstraints implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3111279786928295127L;

  /** Set of factories to be ignored. */
  private Set<Factory<?>> ignoreList = new HashSet<>();

  /**
   * Adds factory to ignore list.
   * 
   * @param factory
   *          the factory to be ignored
   */
  public void ignore(Factory<?> factory) {
    ignoreList.add(factory);
  }

  /**
   * Removes factory from ignore list.
   * 
   * @param factory
   *          factory to be considered again
   */
  public void remove(Factory<?> factory) {
    ignoreList.remove(factory);
  }

  /**
   * Applies the constraint to the given {@link FactoryVertex}.
   * 
   * @param facVertex
   *          the factory vertex to which the constraints shall be applied
   */
  protected void apply(FactoryVertex<?> facVertex) {
    facVertex.getFactories().removeAll(ignoreList);
  }

  public Set<Factory<?>> getIgnoreList() {
    return ignoreList;
  }

  public void setIgnoreList(Set<Factory<?>> ignoreList) {
    this.ignoreList = ignoreList;
  }

  public int getIngoreListSize() {
    return ignoreList.size();
  }

}
