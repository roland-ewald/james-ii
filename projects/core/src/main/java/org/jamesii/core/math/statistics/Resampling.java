/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Auxiliary re-sampling methods.
 * 
 * @author Roland Ewald
 * 
 */
public class Resampling {

  /**
   * Class should not be instantiated.
   */
  private Resampling() {
  }

  /**
   * Implements 0,632 bootstrapping (e.g. see Frank and Witten, "Data Mining:
   * Practical Machine Learning Tools and Techniques". Morgan Kaufmann 2005).
   * 
   * @param <X>
   * 
   * @param original
   *          list with original data
   * @param target
   *          collection in which to store re-sampled entries
   * @param random
   *          random number generator to be used
   * @return set of indices, indicating which elements of the list have been
   *         placed in the target collection
   */
  public static <X> Set<Integer> bootStrap(List<X> original,
      Collection<X> target, IRandom random) {
    int size = original.size();
    Set<Integer> indices = new HashSet<>();
    int index = 0;
    for (int i = 0; i < size; i++) {
      index = Math.abs(random.nextInt()) % size;
      indices.add(index);
      target.add(original.get(index));
    }
    return indices;
  }
}
