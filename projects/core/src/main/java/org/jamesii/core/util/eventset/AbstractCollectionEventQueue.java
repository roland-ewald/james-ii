/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.Collection;
import java.util.Iterator;

/**
 * Abstract class for all event queue implementations based on a simple
 * collection. Provides simple default implementations for a number of the
 * methods defined in the interface based on the collection interface.
 * Subclasses might replace these if the concrete data structure used allows a
 * more efficient method to retrieve the data.
 * 
 * @author Jan Himmelspach
 * @author Arne Bittig
 * 
 * @param <E>
 *          Entry type
 * @param <T>
 *          Time base type
 * @param <D>
 *          Data type (i.e. type of collection used internally)
 */
public abstract class AbstractCollectionEventQueue<E, T extends Comparable<T>, D extends Collection<Entry<E, T>>>
    extends AbstractEventQueue<E, T> implements Iterable<Entry<E, T>> {

  private static final long serialVersionUID = -1443897334400729651L;

  private final D data;

  /**
   * @param collection
   *          The collection to use to store data internally
   */
  protected AbstractCollectionEventQueue(D collection) {
    this.data = collection;
  }

  @Override
  public T dequeue(E event) {
    Iterator<Entry<E, T>> eIt = getData().iterator();
    while (eIt.hasNext()) {
      Entry<E, T> entry = eIt.next();
      if (entry.getEvent() == event) {
        eIt.remove();
        return entry.getTime();
      }
    }
    return null;
  }

  @Override
  public T getTime(E event) {

    for (Entry<E, T> entry : getData()) {
      if (entry.getEvent() == event) {
        return entry.getTime();
      }
    }

    return null;
  }

  @Override
  public boolean isEmpty() {
    return getData().isEmpty();
  }

  @Override
  public int size() {
    return getData().size();
  }

  @Override
  public void enqueue(E event, T time) {
    getData().add(new Entry<>(event, time));
  }

  @Override
  public void requeue(E event, T newTime) {
    dequeue(event);
    enqueue(event, newTime);
  }

  @Override
  public void requeue(E event, T oldTime, T newTime) {
    dequeue(event);
    enqueue(event, newTime);
  }

  /**
   * @return the data
   */
  protected final D getData() {
    return data;
  }

  @Override
  public Iterator<Entry<E, T>> iterator() {
    return new Iterator<Entry<E, T>>() {
      private final Iterator<Entry<E, T>> it = getData().iterator();

      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public Entry<E, T> next() {
        return it.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

}
