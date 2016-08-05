/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
public class TreeAndHashMapSetEventQueue<E, T extends Comparable<T>> extends
AbstractMapEventQueue<E, T> {

  private static final long serialVersionUID = -3896179177391736029L;

  private final NavigableMap<T, Collection<E>> timeEvMap = new TreeMap<>();

  /** Default constructor (no customizable settings). */
  public TreeAndHashMapSetEventQueue() {
    super(new HashMap<E, T>());
  }

  @Override
  public Entry<E, T> dequeue() {
    T time = getMin();
    if (time == null) {
      return null;
    }
    Collection<E> l = timeEvMap.get(time);
    E event;
    if (l.size() == 1) {
      timeEvMap.remove(time);
      event = l.iterator().next();
    } else {
      Iterator<E> it = l.iterator();
      event = it.next();
      it.remove();
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
   * Remove event with given time from internal map without checking whether
   * updating cached first time & list is needed
   *
   * @param time
   * @param event
   * @return true iff event was only event with given time stamp
   */
  protected boolean removeFromTimeEvMap(T time, E event) {
    Collection<E> l = timeEvMap.get(time);
    if (l.size() == 1) {
      timeEvMap.remove(time);
      return true;
    } else {
      l.remove(event); // CHECK: assert that ret val is true ?!
      return false;
    }
  }

  @Override
  public List<E> dequeueAll() {
    if (getEventTimeMap().isEmpty()) {
      return Collections.emptyList();
    }
    Map.Entry<T, Collection<E>> firstEntry = timeEvMap.firstEntry();
    timeEvMap.remove(firstEntry.getKey());
    Collection<E> rv = firstEntry.getValue();
    for (E e : rv) {
      getEventTimeMap().remove(e);
    }
    return new ListWrappingCollection<>(rv);
  }

  @Override
  public List<E> dequeueAll(T time) {
    Collection<E> coll = timeEvMap.remove(time);
    if (coll == null) {
      return Collections.emptyList();
    }
    for (E e : coll) {
      getEventTimeMap().remove(e);
    }
    return new ListWrappingCollection<>(coll);
  }

  @Override
  public void enqueue(E event, T time) {
    T oldTime = getEventTimeMap().put(event, time);
    if (oldTime != null) {
      if (oldTime.equals(time)) {
        return;
      } else {
        removeFromTimeEvMap(oldTime, event);
      }
    }
    putInTimeEvMap(time, event);

  }

  /**
   * Put event at given time into the time->event map, return
   *
   * @param time
   * @param event
   * @return collection of events at same time (incl. added one)
   */
  protected Collection<E> putInTimeEvMap(T time, E event) {
    Collection<E> l = timeEvMap.get(time);
    if (l == null) {
      l = new LinkedHashSet<>();
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
