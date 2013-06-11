/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.collection.list.SortedList;

/**
 * The Class SortedListEventQueue. <br>
 * Internally a {@link org.jamesii.core.util.collection.list.SortedList} is used
 * to hold the data. This means that binary operations (O (log n)) are used to
 * locate positions whenever possible (i.e., on insertion, on requeue (oldTime),
 * dequeue (time)). {@link #getMin()} is O(1), dequeue (besides internal array
 * organization) is in O(1), too. Enqueue is in O(log n).
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
 * @param <E>
 *          Event type
 * @param <T>
 *          Time data type
 */
public class SortedListEventQueue<E, T extends Comparable<T>> extends
    AbstractEventQueue<E, T> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8740517685973910612L;

  /** The data. */
  private SortedList<Entry<E, T>> data = new SortedList<>();

  @Override
  public Entry<E, T> dequeue() {
    if (isEmpty()) {
      return null;
    }

    return data.extractTop();
  }

  @Override
  public T dequeue(E event) {
    int index = -1;
    for (int i = 0; i < data.size(); i++) {
      if (data.get(i).getEvent() == event) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      // T result = data.get(index).getTime();
      // data.remove(index);
      // return result;
      return data.remove(index).getTime();
    }

    return null;
  }

  @Override
  public List<E> dequeueAll() {
    List<E> result = new ArrayList<>();
    if (isEmpty()) {
      return result;
    }
    Entry<E, T> min = data.extractTop();
    result.add(min.getEvent());

    while ((data.size() > 0)
        && (min.getTime().compareTo(data.top().getTime()) == 0)) {
      result.add(data.extractTop().getEvent());
    }

    return result;
  }

  @Override
  public List<E> dequeueAll(T time) {

    List<E> result = new ArrayList<>();

    if (isEmpty()) {
      return result;
    }

    // find left most occurence of time
    int pos = data.getPos(new Entry<E, T>(null, time));

    List<Integer> minIndex = new ArrayList<>();

    for (int i = pos; i < data.size(); i++) {
      int comp = time.compareTo(data.get(i).getTime());
      if (comp == 0) {
        result.add(data.get(i).getEvent());
        minIndex.add(i);
      } else {
        break;
      }

    }

    for (int i = minIndex.size() - 1; i >= 0; i--) {
      data.remove((int) minIndex.get(i));
    }

    return result;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    Map<E, Object> result = new HashMap<>();
    if (isEmpty()) {
      return result;
    }
    Entry<E, T> min = data.extractTop();
    result.put(min.getEvent(), null);

    while ((data.size() > 0)
        && (min.getTime().compareTo(data.top().getTime()) == 0)) {
      result.put(data.extractTop().getEvent(), null);
    }

    return result;
  }

  @Override
  public void enqueue(E event, T time) {
    data.add(new Entry<>(event, time));
  }

  @Override
  public T getMin() {
    if (isEmpty()) {
      return null;
    }

    return data.top().getTime();
  }

  @Override
  public T getTime(E event) {
    int index = -1;
    for (int i = 0; i < data.size(); i++) {
      if (data.get(i).getEvent() == event) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      T result = data.get(index).getTime();
      return result;
    }

    return null;
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public void requeue(E event, T newTime) {
    dequeue(event);
    enqueue(event, newTime);
  }

  @Override
  public void requeue(E event, T oldTime, T newTime) {
    Entry<E, T> e = new Entry<>(event, oldTime);
    int pos = data.getPos(e);
    if (pos < data.size() // needed to handle wrong old time gracefully
        && e.compareTo(data.get(pos)) == 0) {
      data.remove(pos);
    }

    enqueue(event, newTime);
  }

  @Override
  public int size() {
    return data.size();
  }

}
