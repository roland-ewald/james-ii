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

/**
 * The Class UnsortedListEventQueue. Besides enqueue, isEmpty, and size all
 * operations are in O(n).<br>
 * Internally a {@link java.util.ArrayList} is used to hold the data.
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
 * <td>O(1)</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeue()}</td>
 * <td>O(n)</td>
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
 * <td>O(n)</td>
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
public class UnsortedListEventQueue<E, T extends Comparable<T>> extends
    AbstractCollectionEventQueue<E, T, List<Entry<E, T>>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5032833053356766973L;

  public UnsortedListEventQueue() {
    super(new ArrayList<Entry<E, T>>());
  }

  @Override
  public Entry<E, T> dequeue() {
    if (isEmpty()) {
      return null;
    }
    Entry<E, T> min = getData().get(0);
    int minIndex = 0;
    for (int i = 1; i < getData().size(); i++) {
      if (min.getTime().compareTo(getData().get(i).getTime()) > 0) {
        min = getData().get(i);
        minIndex = i;
      }
    }

    getData().remove(minIndex);
    return min;
  }

  @Override
  public T dequeue(E event) {
    int index = -1;
    for (int i = 0; i < getData().size(); i++) {
      if (getData().get(i).getEvent() == event) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      T result = getData().get(index).getTime();
      getData().remove(index);
      return result;
    }

    return null;
  }

  @Override
  public List<E> dequeueAll() {
    List<E> result = new ArrayList<>();
    if (isEmpty()) {
      return result;
    }
    Entry<E, T> min = getData().get(0);
    result.add(min.getEvent());
    List<Integer> minIndex = new ArrayList<>();
    minIndex.add(0);
    for (int i = 1; i < getData().size(); i++) {
      int comp = min.getTime().compareTo(getData().get(i).getTime());
      if (comp > 0) {
        result.clear();
        minIndex.clear();

        min = getData().get(i);
        result.add(min.getEvent());
        minIndex.add(i);
      } else {
        if (comp == 0) {
          result.add(getData().get(i).getEvent());
          minIndex.add(i);
        }
      }
    }

    for (int i = minIndex.size() - 1; i >= 0; i--) {
      getData().remove((int) minIndex.get(i));
    }

    return result;
  }

  @Override
  public List<E> dequeueAll(T time) {
    List<E> result = new ArrayList<>();
    if (isEmpty()) {
      return result;
    }
    List<Integer> minIndex = new ArrayList<>();
    for (int i = 0; i < getData().size(); i++) {
      int comp = time.compareTo(getData().get(i).getTime());
      if (comp == 0) {
        result.add(getData().get(i).getEvent());
        minIndex.add(i);
      }

    }

    for (int i = minIndex.size() - 1; i >= 0; i--) {
      getData().remove((int) minIndex.get(i));
    }

    return result;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    Map<E, Object> result = new HashMap<>();
    if (isEmpty()) {
      return result;
    }
    Entry<E, T> min = getData().get(0);
    result.put(min.getEvent(), null);
    List<Integer> minIndex = new ArrayList<>();
    minIndex.add(0);
    for (int i = 1; i < getData().size(); i++) {
      int comp = min.getTime().compareTo(getData().get(i).getTime());
      if (comp > 0) {
        result.clear();
        minIndex.clear();

        min = getData().get(i);
        result.put(min.getEvent(), null);
        minIndex.add(i);
      } else {
        if (comp == 0) {
          result.put(getData().get(i).getEvent(), null);
          minIndex.add(i);
        }
      }
    }

    for (int i = minIndex.size() - 1; i >= 0; i--) {
      getData().remove((int) minIndex.get(i));
    }

    return result;
  }

  @Override
  public T getMin() {
    if (isEmpty()) {
      return null;
    }
    Entry<E, T> min = getData().get(0);
    for (int i = 1; i < getData().size(); i++) {
      if (min.getTime().compareTo(getData().get(i).getTime()) > 0) {
        min = getData().get(i);
      }
    }
    return min.getTime();
  }

  @Override
  public T getTime(E event) {
    int index = -1;
    for (int i = 0; i < getData().size(); i++) {
      if (getData().get(i).getEvent() == event) {
        index = i;
        break;
      }
    }

    if (index != -1) {
      T result = getData().get(index).getTime();
      return result;
    }

    return null;
  }

}
