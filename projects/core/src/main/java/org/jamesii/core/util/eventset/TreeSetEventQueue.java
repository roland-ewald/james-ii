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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The Class TreeSetEventQueue. <br>
 * Internally a {@link java.util.TreeSet} is used to hold the data.
 * 
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
 * <td>O()</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeue()}</td>
 * <td>O(1)</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeueAll()}</td>
 * <td>O()</td>
 * </tr>
 * <tr>
 * <td>{@link #requeue(Object, Comparable)}</td>
 * <td>O()</td>
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
 */
public class TreeSetEventQueue<E, T extends Comparable<T>> extends
    AbstractCollectionEventQueue<E, T, SortedSet<Entry<E, T>>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 449964186107779822L;

  public TreeSetEventQueue() {
    super(new TreeSet<Entry<E, T>>());
  }

  private Entry<E, T> extractTop() {
    Entry<E, T> result = getData().first();
    Iterator<Entry<E, T>> it = getData().iterator();
    it.next();
    it.remove();
    return result;
  }

  @Override
  public Entry<E, T> dequeue() {
    if (isEmpty()) {
      return null;
    }

    return extractTop();
  }

  @Override
  public List<E> dequeueAll() {
    List<E> result = new ArrayList<>();
    if (isEmpty()) {
      return result;
    }
    Entry<E, T> min = getData().first();
    result.add(min.getEvent());

    Iterator<Entry<E, T>> it = getData().iterator();
    it.next(); // select min element
    it.remove(); // removed first element

    while (it.hasNext()) {
      Entry<E, T> cur = it.next();

      if (min.getTime().compareTo(cur.getTime()) == 0) {
        it.remove();
        result.add(cur.getEvent());
      } else {
        break;
      }
    }

    return result;
  }

  @Override
  public List<E> dequeueAll(T time) {

    List<E> result = new ArrayList<>();

    if (isEmpty()) {
      return result;
    }

    boolean first = false;
    Entry<E, T> entry = null;
    Iterator<Entry<E, T>> eIt = getData().iterator();
    while (eIt.hasNext()) {
      entry = eIt.next();
      if (entry.getTime().compareTo(time) == 0) {
        first = true;
        result.add(entry.getEvent());
        eIt.remove();
        break;
      }
    }

    if (!first) {
      return result;
    }

    while (eIt.hasNext()) {
      entry = eIt.next();
      if (entry.getTime().compareTo(time) == 0) {
        result.add(entry.getEvent());
        eIt.remove();
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

    Entry<E, T> min = getData().first();

    result.put(min.getEvent(), null);

    Iterator<Entry<E, T>> it = getData().iterator();
    it.next(); // select min element
    it.remove(); // removed first element

    while (it.hasNext()) {
      Entry<E, T> cur = it.next();

      if (min.getTime().compareTo(cur.getTime()) == 0) {
        it.remove();
        result.put(cur.getEvent(), null);
      } else {
        break;
      }
    }

    return result;
  }

  @Override
  public T getMin() {
    if (isEmpty()) {
      return null;
    }

    return getData().first().getTime();
  }

}
