/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.sorting;

import java.util.List;

/**
 * The Interface ISortingAlgorithm. Should be implemented by all sorting
 * algorithms so that they can be arbitrarily exchanged.
 * 
 * @author Jan Himmelspach
 */
public interface ISortingAlgorithm {

  /**
   * Sort the passed list of comparable items.
   * 
   * @param <A>
   *          The type of the list entries - needs to implement Comparable<A>
   * 
   * 
   * @param list
   *          the list to be sorted.
   * @param ascending
   *          if true sort ascending, otherwise descending
   */
  <A extends Comparable<A>> void sortInPlace(List<A> list, boolean ascending);

  /**
   * Sort the passed list of comparable items. The input list is not modified.
   * 
   * @param <A>
   *          The type of the list entries - needs to implement Comparable<A>
   * 
   * @param list
   *          the list to be sorted.
   * @param ascending
   *          if true sort ascending, otherwise descending
   * @return the sorted list,
   */
  <A extends Comparable<A>> List<A> sort(List<A> list, boolean ascending);

}
