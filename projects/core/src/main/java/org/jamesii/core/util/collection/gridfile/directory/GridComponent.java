/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.gridfile.directory;

import java.util.List;
import java.util.Map;

public abstract class GridComponent<T> {

  /**
   * Copy the structure of this component. The elements aren't copied!
   */
  public abstract GridComponent<T> copy();

  /**
   * Return all elements in the leaves of this component. The elements are
   * removed from the lists!
   */
  public abstract List<Map<T, T>> take();

  /**
   * Return the leaf at the coords
   */
  public abstract GridLeaf<T> getLeaf(int[] coords);

  /**
   * Return the component with the given coordinates at the given level.
   */
  public abstract GridComponent<T> getComponent(int[] coords, int level);

  /**
   * Return the elements of a partial match. The different lists are merged to
   * one. If one coordinate is -1, this dimension is not restrictive. The depth
   * is the current depth of the node/leaf.
   */
  public abstract Map<T, T> partialMatch(int[] coords, int depth);

  /**
   * Delete all elements from the leaves.
   */
  public abstract void clear();

  /**
   * Return all element lists.
   */
  public abstract List<Map<T, T>> getLists();

}
