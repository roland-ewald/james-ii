/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jamesii.core.util.sorting.SortOrder;

/**
 * A sorted list wrapper using a given {@link List} as backend. This way it is
 * possible to provide a special domain specific {@link List} implementation.
 * For instance if there is to be a fairly high amount of removal or addition
 * operations a {@link LinkedList} could be used as backend. If the list is
 * rather static with a high amount of get operations an index based
 * {@link List} such as {@link java.util.ArrayList} could be used.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          the generic class for the list, must be {@link Comparable}
 */
public class SortedList2<E> implements List<E> {

  /**
   * The wrapped backend list.
   */
  private final List<E> wrapped;

  /**
   * The sorting order. Either {@link SortOrder#ASCENDING} or
   * {@link SortOrder#DESCENDING}
   */
  private SortOrder order;

  /** The comparator. */
  private Comparator<E> comparator;

  /**
   * Instantiates a new sorted list using a given list as backend. Ordering
   * would be {@link SortOrder#ASCENDING}
   * 
   * @param backend
   *          the backend to use
   */
  public SortedList2(List<? extends Comparable<? super E>> backend) {
    this(backend, SortOrder.ASCENDING);
  }

  /**
   * Instantiates a new sorted list using a given list as backend.
   * 
   * @param backend
   *          the backend to use
   * @param order
   *          the sorting order to use
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public SortedList2(List<? extends Comparable<? super E>> backend,
      SortOrder order) {
    this((List) backend, new ComparableComparator(), order);
  }

  /**
   * Instantiates a new sorted list using a given list as backend.
   * 
   * @param backend
   *          the backend to use
   * @param comparator
   *          the custom comparator to use
   * @param order
   *          the sorting order to use
   */
  public SortedList2(List<E> backend, Comparator<E> comparator, SortOrder order) {
    super();
    this.wrapped = backend;
    this.comparator = comparator;
    setSortOrder(order == null ? SortOrder.ASCENDING : order);
  }

  @Override
  public boolean add(E e) {
    // find place to add e to
    int index = 0;
    switch (order) {
    case ASCENDING:
      index = Collections.binarySearch(wrapped, e, comparator);
      break;
    case DESCENDING:
      index =
          Collections.binarySearch(wrapped, e,
              Collections.reverseOrder(comparator));
      break;
    default:
      throw new IllegalStateException();
    }
    if (index < 0) {
      index = -index - 1;
    }
    wrapped.add(index, e);
    return true;
  }

  @Override
  public void add(int index, E element) {
    throw new UnsupportedOperationException("Not supported on sorted List");
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
  public boolean addAll(int index, Collection<? extends E> c) {
    throw new UnsupportedOperationException("Not supported on sorted List");
  }

  @Override
  public void clear() {
    wrapped.clear();
  }

  @Override
  public boolean contains(Object o) {
    return wrapped.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return wrapped.containsAll(c);
  }

  @Override
  public E get(int index) {
    return wrapped.get(index);
  }

  @Override
  public int indexOf(Object o) {
    return wrapped.indexOf(o);
  }

  @Override
  public boolean isEmpty() {
    return wrapped.isEmpty();
  }

  @Override
  public Iterator<E> iterator() {
    return wrapped.iterator();
  }

  @Override
  public int lastIndexOf(Object o) {
    return wrapped.lastIndexOf(o);
  }

  /**
   * Sets the sorting order.
   * 
   * @param order
   *          the new sorting order, either {@link SortOrder#ASCENDING} or
   *          {@link SortOrder#DESCENDING}
   */
  public final void setSortOrder(SortOrder order) {
    if (order == this.order || order == null) {
      return;
    }

    this.order = order;

    switch (order) {
    case ASCENDING:
      Collections.sort(wrapped, comparator);
      break;
    case DESCENDING:
      Collections.sort(wrapped, Collections.reverseOrder(comparator));
      break;
    default:
      throw new IllegalStateException();
    }
  }

  @Override
  public ListIterator<E> listIterator() {
    return wrapped.listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return wrapped.listIterator(index);
  }

  @Override
  public boolean remove(Object o) {
    return wrapped.remove(o);
  }

  @Override
  public E remove(int index) {
    return wrapped.remove(index);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return wrapped.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return wrapped.retainAll(c);
  }

  @Override
  public E set(int index, E element) {
    throw new UnsupportedOperationException("Not supported on sorted List");
  }

  @Override
  public int size() {
    return wrapped.size();
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return wrapped.subList(fromIndex, toIndex);
  }

  @Override
  public Object[] toArray() {
    return wrapped.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return wrapped.toArray(a);
  }

  @Override
  public String toString() {
    return wrapped.toString();
  }
}
