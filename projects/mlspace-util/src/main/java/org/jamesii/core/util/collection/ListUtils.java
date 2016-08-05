/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Some utility methods to deal with lists, especially lists of lists
 *
 * @author Arne Bittig
 * @date 04.10.2013
 */
public final class ListUtils {

  private ListUtils() {
  }

  /**
   * "Transpose" a list of lists such that for argument <code>arg</code> and
   * return value <code>rv</code>, for each <code>int</code> <code>i</code> and
   * <code>i</code>, <code>arg.get(i).get(j) == rv.get(j).get(i)</code>. Only
   * works if <code>arg</code>'s element lists all have the same length.
   *
   * @param matrix
   *          List of lists corresponding to matrix
   * @return Lists of lists corresponding to transposed matrix
   * @throws IllegalArgumentException
   *           if not all member lists of matrix have the same length
   */
  public static <T> List<List<T>> transpose(List<? extends List<T>> matrix) {
    if (matrix.isEmpty()) {
      return Collections.emptyList();
    }
    int nCols = matrix.get(0).size();
    int nRows = matrix.size();
    for (int iRow = 1; iRow < nRows; iRow++) {
      if (matrix.get(iRow).size() != nCols) {
        throw new IllegalArgumentException("Argument is not a matrix");
      }
    }
    List<List<T>> rv = new ArrayList<>(nCols);
    for (int iCol = 0; iCol < nCols; iCol++) {
      List<T> newRow = new ArrayList<>(nRows);
      for (int iRow = 0; iRow < nRows; iRow++) {
        newRow.add(matrix.get(iRow).get(iCol));
      }
      rv.add(newRow);
    }
    return rv;
  }

  /**
   * Take a list of collections, create a new list starting with an element from
   * the first such collection, followed by an element from the second
   * collection, then an element from the third,... . In fact, create all
   * possible such lists.
   *
   * For example, for {x,y} and {0,1,2} passed (in Java-appropriate collection
   * form) all combinations are x0, x1, x2, y0, y1, y2 (although returned in
   * Java list form and not necessarily in that order). If an input collection
   * contains an element multiple times, combinations containing this element at
   * the respective position will also be in the returned list multiple times.
   *
   * @see the Axiom of Choice ;-)
   * @param toCombine
   *          List of collections to choose one element each from
   * @return List of all possible combinations
   */
  public static <T> List<? extends List<T>> combinations(
      List<? extends Iterable<? extends T>> toCombine) {
    List<ArrayList<T>> list = new ArrayList<>();
    if (!toCombine.isEmpty()) {
      list.add(new ArrayList<T>());
      recursiveCombinations(toCombine, 0, list);
    }
    return list;
  }

  /**
   * Do actual work for {@link #combinations(List)}
   *
   * @param toCombine
   *          List of Collections with data to combine (i.e. input of
   *          {@link #combinations(List)})
   * @param startIndex
   *          Index of lists where processing should start (0 initially)
   * @param combs
   *          Combinations (designated return value); must initially be an
   *          ArrayList of ArrayLists containing a single, empty ArrayList
   */
  private static <T> void recursiveCombinations(
      List<? extends Iterable<? extends T>> toCombine, int startIndex,
      List<ArrayList<T>> combs) { // NOSONAR: clone called
    Iterable<? extends T> thisList = toCombine.get(startIndex);
    for (int outI = combs.size() - 1; outI >= 0; outI--) {
      ArrayList<T> listToAddTo = combs.get(outI);
      T firstEl = null;
      boolean isFirst = true; // firstEl may actually be assigned null...
      for (T el : thisList) {
        if (isFirst) {
          firstEl = el; // ...here!
          isFirst = false;
        } else {
          ArrayList<T> lClone = (ArrayList<T>) listToAddTo.clone();
          lClone.add(el);
          combs.add(lClone);
        }
      }
      listToAddTo.add(firstEl);
    }
    if (startIndex < toCombine.size() - 1) {
      recursiveCombinations(toCombine, startIndex + 1, combs);
    }
  }

  // /**
  // * Main method to demonstrate/test {@link #combinations(List...)}/
  // * {@link #combinations(List)} and {@link #transpose(List)}
  // *
  // * @param args
  // * ignored main method args
  // */
  // @SuppressWarnings("unchecked")
  // public static void main(String[] args) {
  // List<? extends List<Object>> combinations = combinations(
  // Arrays.asList(1, 2), Arrays.asList('a', 'b', 'c'),
  // Arrays.<Object> asList("X", "Y"));
  // System.out.println(combinations);
  // List<?> cc = (List<?>) ((ArrayList<?>) combinations).clone();
  // Collections.shuffle(cc, new WrappedJamesRandom(new JavaRandom(25)));
  // System.out.println(cc);
  // Collections.shuffle(combinations, new Random(25));
  // System.out.println(combinations);
  // // returned, as of Aug 22, 2012:
  // // [[1, a, X], [2, a, X], [2, b, X], [2, c, X], [1, b, X], [1, c, X],
  // // [1, c, Y], [1, b, Y], [2, c, Y], [2, b, Y], [2, a, Y], [1, a, Y]]
  // System.out.println(transpose(combinations));
  // System.out.println(combinations(Arrays.asList('x', 'y'),
  // Arrays.asList(0, 1, 2)));
  // }
}
