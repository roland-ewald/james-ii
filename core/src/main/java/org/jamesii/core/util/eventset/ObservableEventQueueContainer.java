/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.core.base.Entity;
import org.jamesii.core.util.collection.list.SortedList;

/**
 * This is a sorted list implementation of an event queue, which can use any
 * other event queue as a back-bone. Only if the container is in 'observation
 * mode' the sorted list will be used (in parallel to the back-bone queue), so
 * that the complete order of the events is observable for the user interface.
 * this means, while observing the performance will be rather bad, since two
 * event lists are actually used, but when turning observation off the
 * performance is that of the back-bone queue.
 * 
 * @author Roland Ewald
 * 
 *         27.04.2007
 * @param <E>
 *          the type of the events to be stored in the queue
 * @param <T>
 *          the type of the "time" used in the queue
 * 
 */
public class ObservableEventQueueContainer<E, T extends Comparable<T>> extends
    Entity implements IEventQueue<E, T> {

  /**
   * Serialisation ID
   */
  private static final long serialVersionUID = -8495028444962474961L;

  /**
   * If observing, all events are stored in here
   */
  private SortedList<Entry<E, T>> eventList = new SortedList<>();

  /**
   * Event queue
   */
  private IEventQueue<E, T> queue = null;

  /**
   * Default constructor.
   * 
   * @param eventQueue
   *          the event queue
   */
  public ObservableEventQueueContainer(IEventQueue<E, T> eventQueue) {
    queue = eventQueue;
  }

  @Override
  public Entry<E, T> dequeue() {
    eventList.remove(0);
    this.changed();
    return queue.dequeue();
  }

  @Override
  public T dequeue(E event) {
    T d = queue.dequeue(event);
    eventList.remove(new Entry<>(event, d));
    this.changed();
    return d;
  }

  @Override
  public List<E> dequeueAll() {
    List<E> dequeuedEvents = queue.dequeueAll();
    for (int i = 0; i < dequeuedEvents.size(); i++) {
      eventList.remove(0);
    }
    this.changed();
    return dequeuedEvents;
  }

  @Override
  public List<E> dequeueAll(T time) {
    List<Integer> indices = getIndicesByTime(time);
    removeIndices(indices);
    this.changed();
    return queue.dequeueAll(time);
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {

    if (eventList.size() == 0) {
      List<Integer> indices = getIndicesByTime(eventList.get(0).getTime());
      removeIndices(indices);
      this.changed();
    }

    return queue.dequeueAllHashed();
  }

  @Override
  public void enqueue(E event, T time) {
    eventList.add(new Entry<>(event, time));
    this.changed();
    queue.enqueue(event, time);
  }

  /**
   * Gets the event.
   * 
   * @param i
   *          the i
   * 
   * @return the event
   */
  public E getEvent(int i) {
    return eventList.get(i).getEvent();
  }

  /**
   * Gets the events.
   * 
   * @return the events
   */
  public SortedList<Entry<E, T>> getEvents() {
    return this.eventList;
  }

  /**
   * Gets the indices by time.
   * 
   * @param time
   *          the time
   * 
   * @return the indices by time
   */
  protected List<Integer> getIndicesByTime(T time) {
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < eventList.size(); i++) {
      Entry<E, T> entry = eventList.get(i);
      if (entry.getTime().equals(time)) {
        indices.add(i);
      }
      if (entry.getTime().compareTo(time) > 0) {
        break;
      }
    }
    return indices;
  }

  @Override
  public T getMin() {
    return queue.getMin();
  }

  @Override
  public T getTime(E event) {
    return queue.getTime(event);
  }

  @Override
  public boolean isEmpty() {
    return queue.isEmpty();
  }

  /**
   * Removes the indices.
   * 
   * @param indices
   *          the indices
   */
  protected void removeIndices(List<Integer> indices) {
    for (int i = indices.size() - 1; i >= 0; i--) {
      eventList.remove(indices.get(i));
    }
  }

  @Override
  public void requeue(E event, T newTime) {
    for (int i = 0; i < eventList.size(); i++) {
      Entry<E, T> entry = eventList.get(i);
      if (entry.getEvent().equals(event)) {
        eventList.remove(entry);
        eventList.add(new Entry<>(event, newTime));
        break;
      }
    }
    this.changed();
    queue.requeue(event, newTime);
  }

  @Override
  public void requeue(E event, T oldTime, T newTime) {
    eventList.remove(new Entry<>(event, oldTime));
    eventList.add(new Entry<>(event, oldTime));
    this.changed();
    queue.requeue(event, oldTime, newTime);
  }

  @Override
  public void setSize(long size) {
    queue.setSize(size);
  }

  @Override
  public int size() {
    return queue.size();
  }
}
