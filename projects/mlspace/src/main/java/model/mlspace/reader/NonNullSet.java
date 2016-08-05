/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Wrapper class for a {@link Set} that does not allow addition of null as
 * element
 *
 * @author Arne Bittig
 * @param <E>
 *          Element type
 * @date 20.02.2013
 */
public class NonNullSet<E> implements Set<E> {

  private final Set<E> set;

  /** Default constructor wrapping a {@link LinkedHashSet} */
  public NonNullSet() {
    this(new LinkedHashSet<E>());
  }

  /**
   * Full constructor
   * 
   * @param set
   *          Set to wrap
   */
  public NonNullSet(Set<E> set) {
    this.set = set;
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return set.contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    return set.iterator();
  }

  @Override
  public Object[] toArray() {
    return set.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return set.toArray(a);
  }

  @Override
  public boolean add(E e) {
    if (e == null) {
      throw new NullPointerException();
    }
    return set.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return set.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return set.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return set.addAll(c);
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
  public void clear() {
    set.clear();
  }

  @Override
  public boolean equals(Object o) {
    return set.equals(o);
  }

  @Override
  public int hashCode() {
    return set.hashCode();
  }

  @Override
  public String toString() {
    return set.toString();
  }
}
