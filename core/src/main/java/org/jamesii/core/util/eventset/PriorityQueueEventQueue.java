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
import java.util.PriorityQueue;

/**
 * The Class PriorityQueueEventQueue. <br>
 * Internally a Java PriorityQueue is used to hold the data. This class is a
 * wrapper around the Java PriorityQueue implementation to make it usable for
 * event queue operations.
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
 * <td>O()</td>
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
 * <td>O()</td>
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
 * 
 * @param <E>
 *          type of the entries
 * @param <T>
 *          type of the "time", needs to be a comparable
 */
public class PriorityQueueEventQueue<E, T extends Comparable<T>> extends
    AbstractCollectionEventQueue<E, T, PriorityQueue<Entry<E, T>>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 449964186107779822L;

  public PriorityQueueEventQueue() {
    super(new PriorityQueue<Entry<E, T>>());
  }

  @Override
  public Entry<E, T> dequeue() {
    return getData().poll();
  }

  @Override
  public List<E> dequeueAll() {
    List<E> result = new ArrayList<>();
    if (isEmpty()) {
      return result;
    }
    Entry<E, T> min = getData().poll();
    result.add(min.getEvent());

    while ((getData().size() > 0)
        && (min.getTime().compareTo(getData().peek().getTime()) == 0)) {
      result.add(getData().poll().getEvent());
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

    Entry<E, T> min = getData().poll();
    result.put(min.getEvent(), null);

    while ((getData().size() > 0)
        && (min.getTime().compareTo(getData().peek().getTime()) == 0)) {
      result.put(getData().poll().getEvent(), null);
    }

    return result;
  }

  @Override
  public T getMin() {
    if (isEmpty()) {
      return null;
    }

    return getData().peek().getTime();
  }

}
