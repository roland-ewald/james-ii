/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.util.List;

/**
 * Auxiliary functions to work with arrays.
 * 
 * Date: 27.06.2007
 * 
 * @author Roland Ewald
 */
public final class Arrays {

  /**
   * Hidden constructor.
   */
  private Arrays() {
  }

  /**
   * Adapted from http://www.source-code.biz/snippets/java/3.htm
   * 
   * Reallocates an array with a new size, and copies the contents of the old
   * array to the new array.
   * 
   * @param <X>
   *          the type of which an array of shall be returned
   * 
   * @param oldArray
   *          the old array, to be reallocated.
   * @param newSize
   *          the new array size.
   * 
   * @return A new array with the same contents.
   */
  @SuppressWarnings("unchecked")
  public static <X> X[] resizeArray(X[] oldArray, int newSize) {
    int oldSize = oldArray.length;
    Class<?> elementType = oldArray.getClass().getComponentType();
    X[] newArray =
        (X[]) java.lang.reflect.Array.newInstance(elementType, newSize);
    int copyLength = Math.min(oldSize, newSize);
    if (copyLength > 0) {
      System.arraycopy(oldArray, 0, newArray, 0, copyLength);
    }
    return newArray;
  }

  /**
   * Copies a list of objects into a new byte array of a given size.
   * 
   * @param dataChunks
   *          list of objects
   * @param size
   *          size the objects will need in memory
   * 
   * @return array of bytes representing these objects
   */
  public static byte[] dataChunksToArray(List<?> dataChunks, int size) {
    byte[] b;
    byte[] byteArray = new byte[size];

    // Let's copy all single byte array object into the large byte array
    int startPos = 0;
    for (int i = 0; i < dataChunks.size(); i++) {
      b = (byte[]) dataChunks.get(i);
      System.arraycopy(b, 0, byteArray, startPos, b.length);
      startPos += b.length;
    }
    return byteArray;
  }

  /**
   * Returns the index of the interval in which the value is situated, i.e., it
   * returns the index of the element in the list which is nearest to xValue but
   * smaller. The List needs to be sorted!!
   * 
   * @param value
   *          where to search
   * @param lower
   *          lower index where to search
   * @param upper
   *          upper index where to search
   * @param list
   *          the list
   * 
   * @return index of element, or the index of the element left to the pos where
   *         the value would have to be
   */
  public static <X extends Comparable<X>> int binarySearch(List<X> list,
      X value, int lower, int upper) {
    int index = (upper + lower) / 2;
    int max = list.size() - 1;
    if (index == 0 && value.compareTo(list.get(0)) < 0) {
      return 0;
    }
    if (index == max && value.compareTo(list.get(0)) > 0) {
      return max;
    }
    // if value is greater than element at middle position
    if (value.compareTo(list.get(index)) >= 0) {
      // if value is lower than next element
      if (value.compareTo(list.get(index + 1)) <= 0) {
        return index;
      }
      return binarySearch(list, value, index + 1, upper);
    }
    // if value is greater than previous element
    if (value.compareTo(list.get(index - 1)) >= 0) {
      return index - 1;
    }
    return binarySearch(list, value, lower, index - 1);
  }

  /**
   * Merge two arrays into one.
   * 
   * @param <T>
   *          the type of the array entries
   * @param t1
   *          array 1
   * @param t2
   *          array 2
   * @return an array containing all elements from t1 and t2
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] merge(T[] t1, T[] t2) {

    Class<?> elementType = t1.getClass().getComponentType();
    T[] result =
        (T[]) java.lang.reflect.Array.newInstance(elementType, t1.length
            + t2.length);

    System.arraycopy(t1, 0, result, 0, t1.length);

    System.arraycopy(t2, 0, result, t1.length, t2.length);

    return result;
  }

  /**
   * Creates an n-dimensional vector with each element being set to 1/n.
   * 
   * @param n
   *          the dimension
   * @return the vector
   */
  public static double[] createUniformNormalizedVector(int n) {
    double[] result = new double[n];
    double weight = 1. / n;
    for (int i = 0; i < n; i++) {
      result[i] = weight;
    }
    return result;
  }

}
