package org.jamesii.core.util.sorting;

import org.jamesii.core.util.sorting.ISortingAlgorithm;
import org.jamesii.core.util.sorting.SimpleSort;

/**
 * The Class TestSimpleSort.
 * 
 * @author Jan Himmelspach
 */
public class TestSimpleSort extends TestSortingAlgorithm {

  @Override
  protected ISortingAlgorithm createAlgorithm() {
    // debugOut = true; //include this line to see more details about the
    // failing test case
    return new SimpleSort();
  }

}
