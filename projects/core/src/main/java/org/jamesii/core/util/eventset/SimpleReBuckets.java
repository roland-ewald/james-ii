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
 * The SimpleReBuckets event queue is a fast implementation of an event queue
 * for the sequential simulator. It uses two different hashmaps - one contains
 * the mapping event - tonie, the second one a mapping tonie - models. The
 * second hashmap is used for computing the min. This implementation is faster
 * as the CuttingDEVSEventQueue iff the number of different tonies is smaller as
 * the number of changed models - thus the list to be iterated must be shorter
 * as the list for which the tonies had to be recalculated. If that's not true
 * the CuttingDEVSEventQueue will perform better.
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
 * <td>{@link #enqueue(Object, Double)}</td>
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
 * <td>{@link #requeue(Object, Double)}</td>
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
 *          the type of the events to be stored in the queue
 */
public class SimpleReBuckets<E> extends AbstractEventQueue<E, Double> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -8663634212054878911L;

  /** The bucket. */
  private final Map<Double, Map<E, Object>> bucket = new HashMap<>();

  /** The events. */
  private final Map<E, Double> events = new HashMap<>();

  /**
   * Instantiates a new simple re buckets.
   */
  public SimpleReBuckets() {
    super();
  }

  @Override
  public Entry<E, Double> dequeue() {
    Double d = getMin();
    Map<E, Object> list = bucket.get(d);
    if (list == null) {
      return null;
    }
    Iterator<E> it = list.keySet().iterator();
    E e = it.next();
    it.remove();
    events.remove(e);
    if (!it.hasNext()) {
      bucket.remove(d);
    }
    return new Entry<>(e, d);

  }

  @Override
  public Double dequeue(E event) {

    Double d = events.get(event);

    events.remove(event);

    Map<E, Object> list = bucket.get(d);

    if (list != null) {
      list.remove(event);
      if (list.size() == 0) {
        bucket.remove(d);
      }
    }

    return d;
  }

  @Override
  public List<E> dequeueAll() {
    return dequeueAll(getMin());
  }

  @Override
  public List<E> dequeueAll(Double time) {
    Map<E, Object> r = bucket.remove(time);
    if (r == null) {
      return new ArrayList<>();
    }
    for (E e : r.keySet()) {
      events.remove(e);
    }
    return new ArrayList<>(r.keySet());
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    return dequeueAllHashed(getMin());
  }

  /**
   * This method returns a list of all bucket for the given time or an empty
   * list if there are no bucket for the given time stamp.
   * 
   * @param time
   *          the time
   * 
   * @return null if this feature is not supported
   */
  public Map<E, Object> dequeueAllHashed(Double time) {
    Map<E, Object> r = bucket.remove(time);
    if (r == null) {
      return new HashMap<>();
    }
    for (E e : r.keySet()) {
      events.remove(e);
    }
    return r;
  }

  @Override
  public void enqueue(E event, Double time) {
    if (time < 0) {
      return; // invalid value
    }
    Map<E, Object> list = bucket.get(time);
    if (list == null) {
      list = new HashMap<>();
      bucket.put(time, list);
    }
    list.put(event, null);
    events.put(event, time);
  }

  @Override
  public Double getMin() {

    Double min = null;

    Iterator<Double> it = bucket.keySet().iterator();

    if (it.hasNext()) {
      min = it.next();
    }

    while (it.hasNext()) {
      Double d = it.next();
      if (min > d) {
        min = d;
      }
    }

    return min;
  }

  @Override
  public Double getTime(E event) {

    Double d = events.get(event);

    return d;
  }

  @Override
  public boolean isEmpty() {
    return bucket.isEmpty();
  }

  @Override
  public void requeue(E event, Double time) {
    dequeue(event);
    enqueue(event, time);
  }

  @Override
  public void requeue(E event, Double oldTime, Double time) {
    dequeue(event);
    enqueue(event, time);
  }

  @Override
  public int size() {
    return events.size();
  }

}
