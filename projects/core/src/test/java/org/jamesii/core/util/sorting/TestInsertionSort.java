/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.sorting;

import org.jamesii.core.util.sorting.ISortingAlgorithm;
import org.jamesii.core.util.sorting.InsertionSort;
import org.jamesii.core.util.sorting.TestSortingAlgorithm;

public class TestInsertionSort extends TestSortingAlgorithm {

  @Override
  protected ISortingAlgorithm createAlgorithm() {
    // TODO Auto-generated method stub
    return new InsertionSort();
  }

}
