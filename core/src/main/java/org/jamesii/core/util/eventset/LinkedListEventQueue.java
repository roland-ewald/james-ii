/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * The Class LinkedListEventQueue. Internally a LinkedList is used to hold the
 * data. The {@link java.util.LinkedList} linked list implementation is used
 * internally. This list implementation requires to iterate through the list
 * twice for a number of operations - something which is avoided in the
 * {@link LinkedList2EventQueue} implementation which is based on a more optimal
 * (for the purpose of event queues) linked list implementation.
 * <p/>
 * This implementation is provided to show how the default data structures
 * provided with Java can be used. These data structures can be considered to be
 * very well tested and thus these queues should be even more reliable than
 * others. In addition the implementations based on "default" data structures
 * are used to examine the performance of these default implementations, e.g.,
 * in comparison to new alternative implementations of the data structures
 * shipping with the core.
 * <p/>
 * Classification<br/>
 * <table>
 * <tr>
 * <td><b>Property</b></td>
 * <td><b>Value</b></td>
 * </tr>
 * <tr>
 * <td>List type</td>
 * <td>1-tier list</td>
 * </tr>
 * <tr>
 * <td colspan="2"><b><i>Operations</i></b></td>
 * </tr>
 * <tr>
 * <td>{@link #enqueue(Object, Comparable)}</td>
 * <td>O(n)</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeue()}</td>
 * <td>O(1)</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeueAll()}</td>
 * <td>O(n)</td>
 * </tr>
 * <tr>
 * <td>{@link #requeue(Object, Comparable)}</td>
 * <td>O(n)</td>
 * </tr>
 * <tr>
 * <td>{@link #getMin()}</td>
 * <td>O(1)</td>
 * </tr>
 * <tr>
 * <td colspan="2"><b><i>Parameters</i></b></td>
 * </tr>
 * <tr>
 * <td>no parameters</td>
 * </tr>
 * </table>
 * 
 * @author Jan Himmelspach
 * @param <E>
 *          the type of events in the queue
 * @param <T>
 *          the type of the time / priority ordering
 */
public class LinkedListEventQueue<E, T extends Comparable<T>> extends
    AbstractCollectionEventQueue<E, T, LinkedList<Entry<E, T>>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3248738589960907626L;

  public LinkedListEventQueue() {
    super(new LinkedList<Entry<E, T>>());
  }

  @Override
  public Entry<E, T> dequeue() {
    if (isEmpty()) {
      return null;
    }

    return getData().remove();
  }

  @Override
  public List<E> dequeueAll() {
    List<E> result = new ArrayList<>();
    if (isEmpty()) {
      return result;
    }

    Entry<E, T> min = getData().remove();
    result.add(min.getEvent());

    while ((!getData().isEmpty())
        && (getData().getFirst().getTime().compareTo(min.getTime()) == 0)) {
      result.add(getData().remove().getEvent());
    }

    return result;
  }

  @Override
  public List<E> dequeueAll(T time) {
    List<E> result = new ArrayList<>();
    if (isEmpty()) {
      return result;
    }
    Iterator<Entry<E, T>> it = getData().iterator();
    while (it.hasNext()) {
      Entry<E, T> entry = it.next();

      if (entry.getTime().compareTo(time) == 0) {

        do {
          result.add(entry.getEvent());
          it.remove();

          if (it.hasNext()) {
            entry = it.next();
          } else {
            entry = null;
          }
        } while ((entry != null) && (entry.getTime().compareTo(time) == 0));
      }
    }

    return result;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {

    Map<E, Object> result = new HashMap<>();
    if (isEmpty()) {
      return result;
    }

    for (E e : dequeueAll()) {
      result.put(e, null);
    }
    return result;
  }

  @Override
  public void enqueue(E event, T time) {

    // if (isEmpty()) data.add (new Entry<E,T>(event, time));

    ListIterator<Entry<E, T>> it = getData().listIterator();

    while (it.hasNext()) {
      Entry<E, T> entry = it.next();

      if (entry.getTime().compareTo(time) >= 0) {
        it.previous();
        break;

      }
    }

    it.add(new Entry<>(event, time));
  }

  @Override
  public T getMin() {
    if (getData().isEmpty()) {
      return null;
    }
    return getData().getFirst().getTime();
  }

}
