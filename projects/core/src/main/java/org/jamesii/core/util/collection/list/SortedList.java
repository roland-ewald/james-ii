/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jamesii.core.util.sorting.SortOrder;

/**
 * The Class SortedList.
 * 
 * Is a simple sorted list based on an array list internally. Uses binary search
 * operations. Sorting can be ascending or descending (
 * {@link org.jamesii.core.util.sorting.SortOrder}.
 * 
 * @param <E>
 *          the type of the events to be stored in the list
 * @author Jan Himmelspach
 */
public class SortedList<E extends Comparable<E>> implements Serializable,
    Collection<E> {

  /** generated serialVersionUID. */
  private static final long serialVersionUID = 4588220938379860909L;

  /**
   * The internally used list, items have to be inserted at the correct
   * position.
   */
  private List<E> list;

  /** Defines Search Order. */
  private SortOrder order = SortOrder.ASCENDING;

  /**
   * Default constructor.
   */
  public SortedList() {
    list = new ArrayList<>();
  }

  /**
   * The Constructor.
   * 
   * @param unsortedData
   *          the unsorted data
   */
  public SortedList(List<E> unsortedData) {
    this();
    addAll(unsortedData);
  }

  /**
   * The Constructor.
   * 
   * @param initialCapacity
   *          the initial capacity
   */
  public SortedList(int initialCapacity) {
    list = new ArrayList<>(initialCapacity);
  }

  /**
   * Instantiates a new sorted list.
   * 
   * @param initialCapacity
   *          the initial capacity
   * @param sortOrder
   *          the sort order
   */
  public SortedList(int initialCapacity, SortOrder sortOrder) {
    this(initialCapacity);
    this.order = sortOrder;
  }

  /**
   * The Constructor.
   * 
   * @param sortOrder
   *          the sort order
   */
  public SortedList(SortOrder sortOrder) {
    this();
    this.order = sortOrder;
  }

  /**
   * Inserts an element into the list.
   * 
   * @param e
   *          the e
   */
  @Override
  public boolean add(E e) {
    int mid = getPos(e);
    list.add(mid, e);
    return true;
  }

  /**
   * Adds all entries from the passed ArrayList to this sorted list.
   * 
   * @param unsortedList
   *          a list of entries, must not be sorted
   */
  public final void addAll(List<E> unsortedList) {
    for (E e : unsortedList) {
      add(e);
    }
  }

  /**
   * Returns the top element of the list (and removes it).
   * 
   * @return the top element
   */
  public E extractTop() {
    if (list.isEmpty()) {
      return null;
    }
    // E result = list.get(0);
    // ;
    return list.remove(0);// result;
  }

  /**
   * Returns the element at the given index. Will not remove the element.
   * 
   * @param index
   *          the index
   * 
   * @return an element
   */
  public E get(int index) {
    return list.get(index);
  }

  /**
   * returns the internal list. warning: modifications to this list can harm the
   * "sorted" criteria!!!!
   * 
   * @return the list
   */
  public List<E> getList() {
    return list;
  }

  /**
   * Binary search, e.g. for finding the place for insertion.
   * 
   * NOTE: getPos need not return the position of the element as such (depending
   * on the type of the comparable object)!
   * 
   * @param e
   *          the e
   * 
   * @return the pos the element e has been found at, or the position where it
   *         should be
   */
  public int getPos(E e) {

    int left = 0;
    int right = list.size();
    int mid = 0;

    while (left < right) {
      mid = (left + right) / 2; // left + (right - left) / 2;
      if ((order == SortOrder.ASCENDING && list.get(mid).compareTo(e) < 0)
          || (order == SortOrder.DESCENDING && list.get(mid).compareTo(e) > 0)) {
        left = mid + 1;
      } else {
        right = mid;
      }
    }

    return left;
  }

  /**
   * Index of.
   * 
   * @param e
   *          the e
   * 
   * @return the int
   */
  public int indexOf(E e) {
    return list.indexOf(e);
  }

  /**
   * return true if the list empty.
   * 
   * @return true, if checks if is empty
   */
  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  /**
   * get an iterator for iterating the internal list.
   * 
   * @return the iterator< e>
   */
  @Override
  public Iterator<E> iterator() {
    return list.iterator();
  }

  /**
   * remove the given element from the list.
   * 
   * @param e
   *          the e
   * 
   * @return true, if removes the
   */
  public boolean remove(E e) {
    int i = getPos(e);
    while ((i < list.size()) && (list.get(i).compareTo(e) != 0)) {
      i++;
    }
    // System.out.println("computed pos "+i);
    if (i >= 0) {
      if ((i == 0) && (list.isEmpty())) {
        return false;
      }
      if (i == list.size()) {
        return false;
      }
      if (list.get(i).compareTo(e) != 0) {
        return false;
      }
      list.remove(i);
      return true;
    }
    return false;
  }

  /**
   * Remove the entry at the given index and return a reference to it.
   * 
   * @param index
   *          the index
   * 
   * @return the E
   */
  public E remove(int index) {
    E e = list.get(index);
    list.remove(index);
    return e;
  }

  /**
   * return the number of elements stored in the list, or 0 if empty.
   * 
   * @return the int
   */
  @Override
  public int size() {
    return list.size();
  }

  /**
   * Returns the first element in the list (does not remove this element!!).
   * 
   * @return the E
   */
  public E top() {
    return list.get(0);
  }

  @Override
  public String toString() {
    return list.toString();
  }

  @Override
  public boolean contains(Object o) {
    return list.contains(o);
  }

  @Override
  public Object[] toArray() {
    return list.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return list.toArray(a);
  }

  @Override
  public boolean remove(Object o) {
    return list.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return list.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    boolean result = true;
    for (E e : c) {
      result &= add(e);
    }
    return result;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return list.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return list.retainAll(c);
  }

  @Override
  public void clear() {
    list.clear();
  }

}
