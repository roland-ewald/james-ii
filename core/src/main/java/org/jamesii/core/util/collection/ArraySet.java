/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper class for an {@link java.util.ArrayList}, implementing the
 * {@link org.jamesii.core.util.collection.IMultiSet} interface. Used by the
 * {@link org.jamesii.core.util.collection.ArrayMap} as internal set.
 * 
 * @author Stefan Leye
 * 
 * @param <E>
 *          type of the elements
 */
public class ArraySet<E> implements IMultiSet<E>, Serializable {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 8116956618683144254L;

  /**
   * The internal list.
   */
  private List<E> set;

  /**
   * Construct ArraySet with an initial capacity equal to the default ArrayList
   * initial capacity.
   */
  public ArraySet() {
    set = new ArrayList<>();
  }

  /**
   * Construct ArraySet with given initial capacity.
   * 
   * @param initialCapacity
   *          Initial capacity
   * @author Arne Bittig
   */
  public ArraySet(int initialCapacity) {
    set = new ArrayList<>(initialCapacity);
  }

  @Override
  public boolean add(E e) {
    return set.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return set.addAll(c);
  }

  @Override
  public void clear() {
    set = new ArrayList<>();
  }

  @Override
  public boolean contains(Object o) {
    return set.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return set.containsAll(c);
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public Iterator<E> iterator() {
    return set.iterator();
  }

  @Override
  public boolean remove(Object o) {
    return set.remove(o);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return set.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return set.retainAll(c);
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public Object[] toArray() {
    return set.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return set.toArray(a);
  }

}
