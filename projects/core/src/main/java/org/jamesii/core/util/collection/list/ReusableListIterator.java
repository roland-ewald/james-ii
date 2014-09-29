/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * This class provides a "reusable" iterator - thus the iterator can be used
 * more than once. If {@link #init()} is called the iterator will jump back to
 * the first entry.
 * 
 * This implementation does not make any copy of the underlying array list, and
 * thus it is pretty efficient. However: it does not support concurrent access
 * to the underlying data structure, and it will not get aware of such an access
 * and of any potential modification. Thus one should only use this class if
 * performance is of very high importance, and only if it is sure to do so.
 * 
 * Since the {@link #next()} method here used {@link List#get(int)} calls,
 * performance will actually decrease if the list to be iterated over is not
 * {@link RandomAccess}.
 * 
 * @author Jan Himmelspach
 * @param <E>
 *          Element type
 */
public class ReusableListIterator<E> implements Iterator<E>, Serializable {

  static final long serialVersionUID = -6144199426035407640L;

  /** The current. */
  private int current = -1;

  /** The elements. */
  private final List<E> elements;

  /** The elements count. */
  private int numElements;

  /** The next. */
  private int next = -1;

  /**
   * Constructor.
   * 
   * @param elements
   *          which should be iterated
   */
  public ReusableListIterator(List<E> elements) {
    this.elements = elements;
    if (elements != null) {
      numElements = elements.size();
    } else {
      numElements = -1;
    }

    next = 0;
  }

  @Override
  public boolean hasNext() {
    return (next < numElements);
  }

  /**
   * Inits the.
   */
  public void init() {
    current = -1;
    next = 0;
  }

  @Override
  public E next() {
    current = next;
    next++;
    if (current >= numElements) {
      throw new NoSuchElementException();
    }
    return elements.get(current);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException(
        "ERROR: Please use a private Iterator for deleting items");
  }

}
