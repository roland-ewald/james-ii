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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Event queue implementation based on an event->time {@link Map} and a
 * time->events {@link java.util.SortedMap sorted map} with an additional
 * pointer to first time stamp and associated events.
 *
 * O(1) {@link #getTime(Object) lookup} and reasonably fast
 * {@link #dequeue(Object)} (i.e. less than O(n)). Most other operations around
 * O(log n), where n may also be the number of <i>different</i> time stamps (for
 * {@link #dequeue()}, {@link #getMin()}), with improved constant factor
 * compared to version without pointer to first time stamp & events).
 *
 * @author Arne Bittig
 * @param <E>
 *          Event type
 * @param <T>
 *          Time stamp type
 * @date 08.02.2013
 */
public class TreeAndHashMapSetFEventQueue<E, T extends Comparable<T>> extends
    AbstractMapEventQueue<E, T> {

  private static final long serialVersionUID = -3896179177391736029L;

  private final NavigableMap<T, Collection<E>> timeEvMap = new TreeMap<>();

  private Collection<E> firstColl = null;

  private T firstTime = null;

  /** Default constructor (no customizable settings). */
  public TreeAndHashMapSetFEventQueue() {
    super(new IdentityHashMap<E, T>());
  }

  @Override
  public Entry<E, T> dequeue() {
    if (firstTime == null) {
      return null;
    }
    T time = firstTime;
    E event;
    if (firstColl.size() == 1) {
      timeEvMap.remove(time);
      event = firstColl.iterator().next();
      reassignFirstTimeAndList();
    } else {
      Iterator<E> it = firstColl.iterator();
      event = it.next();
      it.remove();
    }
    getEventTimeMap().remove(event);
    return new Entry<>(event, time);
  }

  private void reassignFirstTimeAndList() {
    if (timeEvMap.isEmpty()) {
      firstTime = null;
      firstColl = null;
    } else {
      Map.Entry<T, Collection<E>> firstEntry = timeEvMap.firstEntry();
      firstTime = firstEntry.getKey();
      firstColl = firstEntry.getValue();
    }
  }

  @Override
  public T dequeue(E event) {
    T time = getEventTimeMap().remove(event);
    if (time != null) {
      removeFromTimeEvMap(time, event);
    }
    return time;
  }

  private void removeFromTimeEvMap(T time, E event) {
    if (removeFromTimeEvMapNoChache(time, event)
        && time.compareTo(firstTime) == 0) {
      reassignFirstTimeAndList();
    }
  }

  /**
   * Remove event with given time from internal map without checking whether
   * updating cached first time & list is needed
   * 
   * @param time
   * @param event
   * @return true iff event was only event with given time stamp
   */
  private boolean removeFromTimeEvMapNoChache(T time, E event) {
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
    if (firstTime == null) {
      return Collections.emptyList();
    }
    Collection<E> rv = timeEvMap.remove(firstTime);
    // assert rv == firstList;
    for (E e : rv) {
      getEventTimeMap().remove(e);
    }
    reassignFirstTimeAndList();
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
    if (time.compareTo(firstTime) == 0) {
      reassignFirstTimeAndList();
    }
    return new ListWrappingCollection<>(coll);
  }

  @Override
  public void enqueue(E event, T time) {
    getEventTimeMap().put(event, time);
    if (firstTime == null || time.compareTo(firstTime) < 0) {
      firstColl = putInTimeEvMap(time, event);
      firstTime = time;
    } else {
      putInTimeEvMap(time, event);
    }
  }

  private Collection<E> putInTimeEvMap(T time, E event) {
    Collection<E> l = timeEvMap.get(time);
    if (l == null) {
      l = new LinkedList<>();
      timeEvMap.put(time, l);
    }
    l.add(event);
    return l;
  }

  @Override
  public T getMin() {
    return firstTime;
  }

}
