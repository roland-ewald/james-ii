package org.jamesii.core.util.sorting;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class BubbleSort.
 * 
 * @author Sven Kluge
 */
public class BubbleSort extends AbstractSortingAlgorithm {

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
    int n = list.size();
    boolean swapped = false;
    do {
      for (int i = 0; i < n - 1; i++) {
        A a = list.get(i);
        A b = list.get(i + 1);
        if (!ascending) {
          if (a.compareTo(b) < 0) {
            swap(list, i, i + 1);
            swapped = true;
          }
        } else {
          if (a.compareTo(b) > 0) {
            swap(list, i, i + 1);
            swapped = true;
          }
        }
      }
      n--;
    } while (swapped && n >= 1);

  }

}
