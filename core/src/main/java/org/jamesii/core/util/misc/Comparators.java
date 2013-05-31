/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.util.Comparator;

/**
 * Utility functions w.r.t. comparison.
 * 
 * @author Roland Ewald
 */
public final class Comparators {

  /**
   * Hidden constructor.
   */
  private Comparators() {
  }

  /**
   * Test two objects for equality.
   * 
   * @param o1
   *          first object
   * @param o2
   *          second object
   * 
   * @return true, if either both are null or both are equal (using
   *         {@link Object#equals(Object)})
   */
  public static boolean equal(Object o1, Object o2) {
    return (o1 == null && o2 == null) || (o1 != null && o1.equals(o2));
  }

  /**
   * Creates comparator for class names.
   * 
   * @return comparator
   */
  public static Comparator<Class<?>> getClassNameComparator() {
    return new Comparator<Class<?>>() {

      @Override
      public int compare(Class<?> o1, Class<?> o2) {
        return o1.getName().compareTo(o2.getName());
      }
    };
  }
}
