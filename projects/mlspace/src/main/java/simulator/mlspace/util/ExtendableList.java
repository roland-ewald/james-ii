/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Wrapper class for lists where the {@link #add(int, Object) add(int,E)} and
 * {@link #set(int, Object) set(int,E)} methods do not fail (throw) if the index
 * is out of range, but rather fill positions up to the desired index with null
 * and sets the next one as desired. Also, the {@link #get(int)} method returns
 * null for indices out of the range of previously set elements instead of
 * throwing an exception. (All other methods are just delegates.)
 *
 * @author Arne Bittig
 * @param <E>
 *          List type parameter
 */
public class ExtendableList<E> implements List<E>, java.io.Serializable {

  private static final long serialVersionUID = 693013580651528791L;

  private final List<E> list;

  /**
   * Constructor with list to use
   * 
   * @param list
   *          List that will be used directly
   */
  private ExtendableList(List<E> list) {
    this.list = list;
  }

  /**
   * Constructor with the default list (new ArrayList<T>())
   */
  public ExtendableList() {
    this(new ArrayList<E>());
  }

  @Override
  public void add(int index, E element) {
    if (index > list.size()) {
      set(index, element);
      return;
    }
    list.add(index, element);
  }

  @Override
  public E get(int index) {
    if (index >= list.size()) {
      return null;
    }
    return list.get(index);
  }

  @SuppressWarnings("unchecked")
  @Override
  public E set(int index, E element) {
    final int sizeDiff = index - this.size();
    if (sizeDiff == 0) {
      list.add(element);
      return null;
    }
    if (sizeDiff > 0) {
      list.addAll(Arrays.asList((E[]) new Object[sizeDiff + 1]));
    }
    return list.set(index, element);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return list.contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    return list.iterator();
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
  public boolean add(E e) {
    return list.add(e);
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
    return list.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    return list.addAll(index, c);
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

  @Override
  public boolean equals(Object o) {
    return list.equals(o);
  }

  @Override
  public int hashCode() {
    return list.hashCode();
  }

  @Override
  public String toString() {
    return list.toString();
  }

  @Override
  public E remove(int index) {
    return list.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return list.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return list.lastIndexOf(o);
  }

  @Override
  public ListIterator<E> listIterator() {
    return list.listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return list.listIterator(index);
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }

}
