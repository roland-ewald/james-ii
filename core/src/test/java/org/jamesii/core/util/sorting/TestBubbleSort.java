/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.sorting;

import org.jamesii.core.util.sorting.BubbleSort;
import org.jamesii.core.util.sorting.ISortingAlgorithm;
import org.jamesii.core.util.sorting.TestSortingAlgorithm;

public class TestBubbleSort extends TestSortingAlgorithm {

  @Override
  protected ISortingAlgorithm createAlgorithm() {
    // debugOut = true;
    return new BubbleSort();
  }

}
