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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jamesii.core.util.collection.list.LinkedList;

/**
 * The Class LinkedListEventQueue. Internally a LinkedList is used to hold the
 * data. The LinkedList used here is the
 * {@link org.jamesii.core.util.collection.list.LinkedList}. Linked lists have
 * the advantage that insertion and removal of event is cheap once the position
 * has been found. <br/>
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
@Deprecated
public class LinkedList2EventQueue<E, T extends Comparable<T>> extends
    AbstractCollectionEventQueue<E, T, LinkedList<Entry<E, T>>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 728411801237182851L;

  public LinkedList2EventQueue() {
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

    if (!isEmpty()) {
      ListIterator<Entry<E, T>> it = getData().listIterator();

      while (it.hasNext()) {
        Entry<E, T> entry = it.next();

        if (entry.getTime().compareTo(time) >= 0) {
          if (it.hasPrevious()) {
            it.previous();
          }
          break;

        }
      }
      it.add(new Entry<>(event, time));
    } else {
      getData().add(new Entry<>(event, time));
    }

  }

  @Override
  public T getMin() {
    if (getData().isEmpty()) {
      return null;
    }
    return getData().getFirst().getTime();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator<Entry<E, T>> it = getData().iterator();
    while (it.hasNext()) {
      Entry<E, T> ent = it.next();
      sb.append(ent.getTime());
      sb.append(' ');
    }
    return sb.toString();
  }

}
