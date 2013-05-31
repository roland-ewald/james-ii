/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.Iterator;

/**
 * Special element set with a special, high performance iterator
 * (privateIterator)
 * 
 * @author Jan Himmelspach
 * 
 * @param <M>
 */
public interface IElementSet<M> {

  /**
   * Return a "normal" Java iterator. Multiple iterators which can be used
   * concurrently can be retrieved.
   * 
   * @return normal, iterator
   */
  Iterator<M> iterator();

  /**
   * Return a "special", reusable iterator. All calls to this method will return
   * the same iterator! Thus using this iterator concurrently will most likely
   * lead to serious faults! This iterator is NOT thread safe. You have to make
   * sure that it is only used by one caller. If you cannot ensure this, you
   * should use the {@link #iterator} method instead. However, the iterator
   * returned here is more efficient - it is faster as the default iterator of
   * normal list implementations.
   * 
   * @return a private iterator.
   * 
   * @see org.jamesii.core.util.collection.list.ReusableListIterator
   */
  Iterator<M> privateIterator();

  /**
   * Return the number of elements in the ElementSet
   * 
   * @return number (0..-1 #elements)
   */
  int size();

}
