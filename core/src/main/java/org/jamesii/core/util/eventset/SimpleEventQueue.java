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

/**
 * Simple and not so efficient event queue management. Stores events in a map
 * ordered by events, i.e., searching the new min takes O(n) time. However,
 * operations on events, e.g., requeue or getTime(event) or dequeue(event) are
 * in O(1).<br>
 * Internally a hash map is used to hold the entries. This means that the order
 * of entries with an equal hash code (and they have the same time value) must
 * not be the same whenever the queue is used (if addresses are used for hash
 * codes).
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
 * @param <E>
 *          the type of the events to be stored in the queue
 * @param <T>
 *          the type of the ordering value - can be any comparable
 * 
 * @author Jan Himmelspach
 */
public class SimpleEventQueue<E, T extends Comparable<T>> extends
    AbstractEventQueue<E, T> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -7714447518255735315L;

  /** The events. */
  private Map<E, T> events = new HashMap<>();

  /**
   * The Constructor.
   */
  public SimpleEventQueue() {
    super();
  }

  @Override
  public Entry<E, T> dequeue() {
    T minValue = null;

    Entry<E, T> ent = null;
    Entry<E, T> entF = null;

    Iterator<Map.Entry<E, T>> eIt = events.entrySet().iterator();

    Boolean first = null;
    if (eIt.hasNext()) {
      first = true;
      Map.Entry<E, T> e = eIt.next();
      minValue = e.getValue();
      entF = new Entry<>(e.getKey(), e.getValue());
    }
    if (first == null) {
      return null;
    }

    while (eIt.hasNext()) {
      Map.Entry<E, T> e = eIt.next();
      if (e.getValue().compareTo(minValue) <= 0) {
        first = false;
        minValue = e.getValue();
        ent = new Entry<>(e.getKey(), e.getValue());
      }
    }

    if (ent != null) {
      events.remove(ent.getEvent());
      return ent;
    }
    if (first) {
      events.remove(entF.getEvent());
      return entF;
    }
    return null;
  }

  @Override
  public T dequeue(E event) {
    T result = events.get(event);
    events.remove(event);
    return result;
  }

  @Override
  public List<E> dequeueAll() {
    return dequeueAll(getMin());
  }

  @Override
  public List<E> dequeueAll(T time) {
    List<E> result = new ArrayList<>();
    for (E m : events.keySet()) {
      if (events.get(m).compareTo(time) == 0) {
        result.add(m);
      }
    }
    for (int i = 0; i < result.size(); i++) {
      events.remove(result.get(i));
    }
    return result;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    return dequeueAllHashed(getMin());
  }

  /**
   * This method returns a list of all events for the given time or null if
   * there are no events for the given time stamp. This method is quite
   * expensive!!! (in O(n))
   * 
   * @param time
   *          the time
   * 
   * @return null if this feature is not supported
   */
  public Map<E, Object> dequeueAllHashed(T time) {
    Map<E, Object> result = new HashMap<>();
    for (E m : events.keySet()) {
      if (events.get(m).compareTo(time) == 0) {
        result.put(m, null);
      }
    }
    for (E m : result.keySet()) {
      events.remove(m);
    }
    return result;
  }

  @Override
  public void enqueue(E event, T time) {

    events.put(event, time);
  }

  @Override
  public T getMin() {

    if (size() == 0) {
      return null;
    }

    Iterator<T> vIt = events.values().iterator();

    T tonie = vIt.next();

    while (vIt.hasNext()) {
      T d = vIt.next();
      if (d.compareTo(tonie) < 0) {
        tonie = d;
      }
    }

    // System.out.println("Min is : "+result);
    return tonie;
  }

  @Override
  public T getTime(E event) {
    return events.get(event);
  }

  @Override
  public boolean isEmpty() {
    return events.isEmpty();
  }

  @Override
  public void requeue(E event, T time) {
    // System.out.println("updating: "+event+" "+time);
    events.put(event, time);
  }

  @Override
  public void requeue(E event, T oldTime, T time) {
    dequeue(event);
    enqueue(event, time);
  }

  @Override
  public int size() {
    return events.size();
  }
}
