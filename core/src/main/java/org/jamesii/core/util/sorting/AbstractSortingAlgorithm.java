/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.sorting;

import java.util.List;

/**
 * The Class AbstractSortingAlgorithm. <br/>
 * Abstract class which can be used as base for the creation of sorting
 * algorithms.
 * 
 * @author Jan Himmelspach
 */
public abstract class AbstractSortingAlgorithm implements ISortingAlgorithm {

  /**
   * Swap the elements at the given positions. <br/>
   * No extra range checking is performed - thus the range checking and rules of
   * the list implementation used apply here. <br/>
   * No size modifications of the list occur, just the positions are exchanged.
   * Swapping is done using the set method of the list interface. *
   * 
   * @param list
   *          the list where the elements are in.
   * @param pos1
   *          the position of the first element (which shall be at pos2 later)
   * @param pos2
   *          the position of the second element (which shall be at pos1 later)
   */
  public static <X> void swap(final List<X> list, final int pos1, final int pos2) {

    List<X> h = list;

    // "move" the element from pos2 to pos1, and backup the element at pos1
    X backup = h.set(pos1, h.get(pos2));

    // now place the backed up element from pos1 at pos2
    h.set(pos2, backup);
  }

}
