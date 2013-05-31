/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Class that wraps a given {@link Iterator} so that it implements the
 * {@link Enumeration} interface.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          specfies the generic type of the given {@link Iterator}
 * 
 */
public class IteratorEnumeration<E> implements Enumeration<E> { // NOSONAR:
                                                                // intention is
                                                                // to actually
                                                                // avoid
                                                                // Enumeration
                                                                // ;)
  /**
   * the iterator to wrap
   */
  private Iterator<E> iterator;

  /**
   * Wrap the given {@link Iterator} to implement the {@link Enumeration}
   * interface
   * 
   * @param it
   *          the iterator to wrap
   */
  public IteratorEnumeration(Iterator<E> it) {
    this.iterator = it;
  }

  /**
   * Wrap the given {@link Iterable} to implement the {@link Enumeration}
   * interface
   * 
   * @param it
   *          the iterator to wrap
   */
  public IteratorEnumeration(Iterable<E> it) {
    this(it.iterator());
  }

  @Override
  public boolean hasMoreElements() {
    return iterator.hasNext();
  }

  @Override
  public E nextElement() {
    return iterator.next();
  }

}
