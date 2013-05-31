/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.jamesii.core.util.collection.Heap;

/**
 * The Class HeapEventQueue.
 * 
 * This event queue is based on a classical heap implementation, thus events are
 * internally stored in a heap data structure. The heap used is
 * {@link org.jamesii.core.util.collection.Heap}. This event queue is a simple
 * 1-tier list implementation.
 * 
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
 * <td>{@link #enqueue(Object, Double)}</td>
 * <td>O(log n)</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeue()}</td>
 * <td>O(log n)</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeueAll()}</td>
 * <td>O()</td>
 * </tr>
 * <tr>
 * <td>{@link #requeue(Object, Double)}</td>
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
 * @param <E>
 *          the type of the events to be stored in the queue
 */
public class HeapEventQueue<E> extends AbstractEventQueue<E, Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8644550485545854087L;

  /** The data. */
  private Heap<Entry<E, Double>> data = new Heap<>();

  @Override
  public Entry<E, Double> dequeue() {
    return data.extractTop();
  }

  @Override
  public Double dequeue(E event) {
    Double t = getTime(event);
    if (t == null) {
      return null;
    }
    data.remove(new Entry<>(event, t));
    return t;
  }

  @Override
  public List<E> dequeueAll() {
    ArrayList<E> result = new ArrayList<>();
    Double t = null; // min time
    Entry<E, Double> e = null;
    do {
      e = data.extractTop();
      if (e == null) {
        break;
      }
      if (t == null) {
        t = e.getTime();
      }
      if (e.getTime().compareTo(t) == 0) {
        result.add(e.getEvent());
      } else {
        break;
      }
    } while (e.getTime().compareTo(t) == 0);
    if (e != null) { // read last element (has a different time stamp)
      data.add(e);
    }
    return result;
  }

  @Override
  public List<E> dequeueAll(Double time) {

    List<E> result = new ArrayList<>();

    List<Entry<E, Double>> list = data.getList();
    for (int i = 1; i <= size(); i++) {
      if (list.get(i).getTime().compareTo(time) == 0) {
        result.add(list.get(i).getEvent());
      }
    }

    for (E e : result) {
      data.remove(new Entry<>(e, time));
    }

    return result;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    List<E> help = dequeueAll();
    Map<E, Object> result = new HashMap<>();

    for (E e : help) {
      result.put(e, null);
    }
    return result;
  }

  @Override
  public void enqueue(E event, Double time) {
    data.add(new Entry<>(event, time));
  }

  @Override
  public Double getMin() {
    Entry<E, Double> e = data.top();
    if (e == null) {
      return null;
    }
    return e.getTime();
  }

  @Override
  public Double getTime(E event) {
    List<Entry<E, Double>> list = data.getList();
    for (int i = 1; i <= size(); i++) {
      if (list.get(i).getEvent() == event) {
        return list.get(i).getTime();
      }
    }
    return null;
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public void requeue(E event, Double time) {
    // System.out.println(event+" "+getTime(event) );
    Double t = getTime(event);
    if (t != null) {
      data.remove(new Entry<>(event, t));
    }
    enqueue(event, time);
  }

  @Override
  public void requeue(E event, Double oldTime, Double time) {
    data.remove(new Entry<>(event, oldTime));
    enqueue(event, time);
  }

  @Override
  public int size() {
    return data.size();
  }

}
