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
 * Utility functions to work around some restrictions for generics. Please use
 * with great care, only in cases where the cast is secure.
 * 
 * @author Roland Ewald
 * 
 */
public final class Generics {

  /**
   * Hidden constructor.
   */
  private Generics() {
  }

  /**
   * Converts an array list of number pairs to an array list.
   * 
   * @param <X>
   *          type of first number
   * @param <Y>
   *          type of second number
   * @param list
   *          the list to be converted
   * @return the same list as
   */
  @SuppressWarnings("unchecked")
  public static <X extends Number, Y extends Number> List<Pair<? extends Number, ? extends Number>> getPlotableList(
      List<Pair<X, Y>> list) {
    // Hopefully the compiler will simply remove this; after removal of
    // generics
    // this reads like (ArrayList)(ArrayList)list;
    return (List<Pair<? extends Number, ? extends Number>>) ((List<?>) list);
  }

  /**
   * Converts a list of one type (Y) to a list of one of its super-types. These
   * could also be interfaces (i.e., there could be several options).
   * 
   * @param <X>
   *          the super-type
   * @param <Y>
   *          the sub-type
   * @param list
   *          the original list
   * @return the new list
   */
  public static <X, Y extends X> List<X> superType(List<Y> list) {
    List<X> newList = new ArrayList<>();
    for (Y o : list) {
      newList.add(o);
    }
    return newList;
  }

  /**
   * A method with the same functionality as {@link Generics#superType(List)},
   * but which does not rely on the extends-relationship between Y and X (rather
   * it uses casting) and therefore does not confuse the JDK 1.6 javac.
   * 
   * TODO: Remove after migration to JDK 1.7
   * 
   * @param list
   * @return the new list
   */
  @SuppressWarnings("unchecked")
  public static <X, Y> List<X> changeListType(List<Y> list) {
    List<X> newList = new ArrayList<>();
    for (Y o : list) {
      newList.add((X) o);
    }
    return newList;
  }

  /**
   * Automatically casts to *any* desired type. This is basically a function
   * intended to avoid adding SuppressWarnings for each *known* unsafe type cast
   * (@SuppressWarnings("unchecked") will kill *all* unsafe type casts within a
   * method/class) . Use with care!
   * 
   * @param <X>
   *          the desired type
   * @param o
   *          the object to be casted
   * @return same object, casted to different type
   * @throws ClassCastException
   *           if you are wrong
   */
  @SuppressWarnings("unchecked")
  public static <X> X autoCast(Object o) {
    return (X) o;
  }

  /**
   * Auto cast array from one type to another. This casting operation is risky
   * and should be used with care. Make sure all elements in the source array
   * can be casted to Y (necessary because of Java's
   * generic-array-restrictions).
   * 
   * @param source
   *          the array whose elements should be casted
   * @param dest
   *          an array of the type into which the elements shall be casted (can
   *          be of size 0!)
   * 
   * @param returns
   *          the resulting (new) array containing all casted elements from
   *          source array
   * 
   * @param <X>
   *          the type of the source array elements
   * @param <Y>
   *          the type of the destination array elements
   */
  @SuppressWarnings("unchecked")
  public static <X, Y> Y[] autoCastArray(X[] source, Y[] dest) {
    Y[] result = Arrays.resizeArray(dest, source.length);
    for (int i = 0; i < source.length; i++) {
      result[i] = (Y) source[i];
    }
    return result;
  }
}
