/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Stefan Rybacki
 * @author Arne Bittig
 * @param <E>
 *          Type of elements
 */
public class CombinedIterator<E> implements Iterator<E> {

  /**
   * Factory method for wrapping combined iterator in an (anonymous)
   * {@link Iterable}, e.g. for enhanced for loops.
   * 
   * @param iterables
   *          Iterables/Collections to iterate over sequentially
   * @return Iterable
   */
  @SafeVarargs
  public static <E1> Iterable<E1> join(
      final Iterable<? extends E1>... iterables) {
    return new Iterable<E1>() {

      @Override
      public Iterator<E1> iterator() {
        return new CombinedIterator<>(iterables);
      }
    };
  }

  private final Queue<Iterator<? extends E>> queue = new LinkedList<>();

  private Iterator<? extends E> current;

  /**
   * Iterator iterating over all elements from the given iterators
   * 
   * @param iterators
   *          Iterators to combine
   */
  @SafeVarargs
  public CombinedIterator(Iterator<? extends E>... iterators) {
    for (Iterator<? extends E> i : iterators) {
      addIterator(i);
    }
    if (queue.isEmpty()) {
      current = Collections.emptyIterator();
    } else {
      current = queue.remove();
    }
  }

  /**
   * Convenience constructor for Iterator iterating over all elements from the
   * given iterables ((lists, sets, queues, ...)
   * 
   * @param iterables
   *          Iterables to iterate over sequentially
   */
  @SafeVarargs
  public CombinedIterator(Iterable<? extends E>... iterables) {
    for (Iterable<? extends E> itble : iterables) {
      addIterator(itble.iterator());
    }
    if (queue.isEmpty()) {
      current = Collections.emptyIterator();
    } else {
      current = queue.remove();
    }
  }

  /**
   * Add (non-empty) iterator to combined iterator
   * 
   * @param it
   *          Iterator
   * @return true if additional elements were added to combined iterator, i.e.
   *         false if it was null or empty
   */
  public final boolean addIterator(Iterator<? extends E> it) {
    if (it == null || !it.hasNext()) {
      return false;
    }
    queue.add(it);
    return true;
  }

  @Override
  public boolean hasNext() {
    while (!current.hasNext() && queue.peek() != null) {
      current = queue.remove();
    }

    return current.hasNext();
  }

  @Override
  public E next() {
    while (!current.hasNext() && queue.peek() != null) {
      current = queue.remove();
    }

    return current.next();
  }

  @Override
  public void remove() {
    current.remove();
  }
}