/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.sorting;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SimpleSort.
 * 
 * Sorting in O(n^2). {@link #sort(List, boolean)} is based on
 * {@link #sortInPlace(List, boolean)}.
 * 
 * "Brute force algorithm" which makes a lot of unnecessary steps.
 * 
 * @author Jan Himmelspach
 */
public class SimpleSort extends AbstractSortingAlgorithm {

  @Override
  public <A extends Comparable<A>> List<A> sort(List<A> list, boolean ascending) {

    if (list == null) {
      return null;
    }

    List<A> result = new ArrayList<>(list);
    sortInPlace(result, ascending);
    return result;
  }

  @Override
  public <A extends Comparable<A>> void sortInPlace(List<A> list,
      boolean ascending) {
    if (list == null) {
      return;
    }
    for (int i = 0; i < list.size(); i++) {
      for (int j = 0; j < list.size(); j++) {
        A a = list.get(i);
        A b = list.get(j);
        if (ascending) {
          if (a.compareTo(b) < 0) {
            swap(list, i, j);
          }
        } else {
          if (a.compareTo(b) > 0) {
            swap(list, i, j);
          }
        }
      }
    }
  }

}
