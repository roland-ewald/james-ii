/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Event queue implementation based on an event->time {@link Map} and a
 * time->events {@link java.util.SortedMap sorted map}.
 *
 * O(1) {@link #getTime(Object) lookup} and reasonably fast
 * {@link #dequeue(Object)} (i.e. less than O(n)). Most other operations around
 * O(log n), where n may also be the number of <i>different</i> time stamps (for
 * {@link #dequeue()}, {@link #getMin()}).
 *
 * @author Arne Bittig
 * @param <E>
 *          Event type
 * @param <T>
 *          Time stamp type
 * @date 08.02.2013
 */
public class TreeAndHashMapEventQueue<E, T extends Comparable<T>> extends
AbstractMapEventQueue<E, T> {

  private static final long serialVersionUID = -3896179177391736029L;

  private final NavigableMap<T, List<E>> timeEvMap = new TreeMap<>();

  /** Default constructor (no customizable settings). */
  public TreeAndHashMapEventQueue() {
    super(new IdentityHashMap<E, T>());
  }

  @Override
  public Entry<E, T> dequeue() {
    T time = getMin();
    if (time == null) {
      return null;
    }
    List<E> l = timeEvMap.get(time);
    E event;
    if (l.size() == 1) {
      timeEvMap.remove(time);
      event = l.get(0);
    } else {
      event = l.remove(0);
    }
    getEventTimeMap().remove(event);
    return new Entry<>(event, time);
  }

  @Override
  public T dequeue(E event) {
    T time = getEventTimeMap().remove(event);
    if (time == null) {
      return null;
    }
    removeFromTimeEvMap(time, event);
    return time;
  }

  /**
   * Remove event with given time from internal map
   *
   * @param time
   * @param event
   * @return true iff event was only event with given time stamp
   */
  protected boolean removeFromTimeEvMap(T time, E event) {
    Collection<E> l = timeEvMap.get(time);
    if (l.size() == 1) {
      timeEvMap.remove(time);
      assert l.contains(event);
      return true;
    } else {
      boolean wasRemoved = l.remove(event);
      assert wasRemoved;
      return false;
    }
  }

  @Override
  public List<E> dequeueAll() {
    if (getEventTimeMap().isEmpty()) {
      return Collections.emptyList();
    }
    Map.Entry<T, List<E>> firstEntry = timeEvMap.firstEntry();
    timeEvMap.remove(firstEntry.getKey());
    List<E> rv = firstEntry.getValue();
    for (E e : rv) {
      getEventTimeMap().remove(e);
    }
    return rv;
  }

  @Override
  public List<E> dequeueAll(T time) {
    List<E> list = timeEvMap.remove(time);
    if (list == null) {
      return Collections.emptyList();
    }
    for (E e : list) {
      getEventTimeMap().remove(e);
    }
    return list;
  }

  @Override
  public void enqueue(E event, T time) {
    putInTimeEvMap(time, event);
    getEventTimeMap().put(event, time);
  }

  /**
   * Put event at given time into the time->event map, return
   *
   * @param time
   * @param event
   * @return collection of events at same time (incl. added one)
   */
  protected List<E> putInTimeEvMap(T time, E event) {
    List<E> l = timeEvMap.get(time);
    if (l == null) {
      l = new LinkedList<>();
      timeEvMap.put(time, l);
    }
    l.add(event);
    return l;
  }

  @Override
  public T getMin() {
    if (timeEvMap.isEmpty()) {
      return null;
    }
    return timeEvMap.firstKey();
  }

}
