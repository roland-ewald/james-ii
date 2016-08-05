/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper class for use when one needs a {@link List} but only has a
 * {@link Collection}. To be constructed with the factory method
 * {@link #wrap(Collection)} that checks whether the argument is an instance of
 * {@link List} and creates no new wrapper object in this case.
 *
 * All methods that {@link List} inherits from {@link Collection} are delegated
 * directly to the collection except for {@link #equals(Object)} and
 * {@link #hashCode()}, which the {@link List} contract is more specific about
 * (see {@link AbstractList#hashCode()} for the latter's implementation relied
 * upon here). Enlarging {@link List}-specificc operations (
 * {@link #add(int, Object)}, {@link #set(int, Object)}) are not supported.
 *
 * Note that the delegation of {@link #add(Object)} may violate the {@link List}
 * contract for the method, e.g. if the wrapped collection is a
 * {@link java.util.Set} and the element in question already present.
 *
 * Note also that {@link #get(int)} and {@link #remove(int)} use the iterator n
 * times (n=argument). The {@link #listIterator()} relies on {@link #get(int)},
 * and all methods relying on either ({@link #lastIndexOf(Object)},
 * {@link #subList(int, int)}, ...) are probably very slow (O(n²) complexity).
 *
 * @param <E>
 *          Entry type
 * @author Arne Bittig
 * @date 15.02.2013
 */
public class ListWrappingCollection<E> extends AbstractList<E> {

  /**
   * Factory method that does only create a wrapper object if the argument is
   * not already a list (and returns the (cast) argument if it is)
   * 
   * @param collection
   *          Collection to wrap (or cast, if it is a list)
   * @return List view of the collection
   */
  public static <E> List<E> wrap(Collection<E> collection) {
    if (collection instanceof List<?>) {
      return (List<E>) collection;
    } else {
      return new ListWrappingCollection<>(collection);
    }
  }

  private final Collection<E> coll;

  protected ListWrappingCollection(Collection<E> collection) {
    this.coll = collection;
  }

  @Override
  public boolean isEmpty() {
    return coll.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return coll.contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    return coll.iterator();
  }

  @Override
  public Object[] toArray() {
    return coll.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return coll.toArray(a);
  }

  @Override
  public boolean add(E e) {
    return coll.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return coll.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return coll.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return coll.addAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return coll.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return coll.retainAll(c);
  }

  @Override
  public void clear() {
    coll.clear();
  }

  @Override
  public int size() {
    return coll.size();
  }

  /**
   * Create iterator, advance to before given position (i.e. index-1 times)
   * 
   * @param index
   *          Number of element to be returned with next next() call
   * @return Iterator
   * @throws IndexOutOfBoundsException
   *           if there are too few elements
   */
  private Iterator<E> iteratorBefore(int index) {
    Iterator<E> it = coll.iterator();
    int idx = 0;
    while (idx++ < index && it.hasNext()) {
      it.next();
    }
    if (idx < index || !it.hasNext()) {
      throw new IndexOutOfBoundsException();
    }
    return it;
  }

  @Override
  public E get(int index) {
    Iterator<E> it = iteratorBefore(index);
    return it.next();

  }

  @Override
  public E remove(int index) {
    Iterator<E> it = iteratorBefore(index);
    E el = it.next();
    it.remove();
    return el;
  }

  @Override
  public int indexOf(Object o) {
    Iterator<E> it = coll.iterator();
    int idx = 0;
    if (o == null) {
      boolean found = false;
      while (it.hasNext() && !found) {
        if (it.next() == null) {
          return idx;
        }
        idx++;
      }
      return -1;
    }
    while (it.hasNext()) {
      if (o.equals(it.next())) {
        return idx;
      }
      idx++;
    }
    return -1;
  }
}
