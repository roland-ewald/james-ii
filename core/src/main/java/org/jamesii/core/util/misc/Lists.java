/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Some auxiliary methods for lists.
 * 
 * @author Florian Marquardt date: 19.02.2008
 */
public final class Lists {

  /**
   * Hidden constructor.
   */
  private Lists() {
  }

  /**
   * This method removes cycles of operator calls in the result as they are
   * obsolete in the planning result Example: Input: (A B C B C B C D E F E)
   * Output: (A B C D E F E)
   * 
   * minimalCylcle is given, maxmalCycle is list.size()/2 i is the window size
   * it is possible to start with the maximal window size and lower it down or
   * start with a minimal window size and increase it it seams that growing the
   * window size results in much less loops than the opposite way.
   * 
   * @param list
   *          the list
   */
  public static void removeCycles(List<?> list) {

    final int minimalCycle = 2;

    // for (int i = this.size() / 2; i >= minimalCycle; i--) {
    for (int i = minimalCycle; i <= list.size() / 2; i++) {
      // window, starts at j and ends at j + i (window size)
      for (int j = 0; j <= list.size() - i * 2; j++) {
        if (list.subList(j, j + i).equals(list.subList(j + i, j + 2 * i))) {
          list.subList(j + i, j + 2 * i).clear();
          // since we pruned the list, the counter must be lowered
          // to ensure that we catch also triples or greater tuples
          // additionally it should not become negative
          j = Math.max(0, j - i);
        }
      }
    }
  }

  /**
   * Creates an n-dimensional vector with each element being set to 1/n.
   * 
   * @param n
   *          the dimension
   * @return the vector as array list
   */
  public static List<Double> createUniformNormalizedVector(int n) {
    List<Double> result = new ArrayList<>();
    double weight = 1. / n;
    for (int i = 0; i < n; i++) {
      result.add(weight);
    }
    return result;
  }

}
