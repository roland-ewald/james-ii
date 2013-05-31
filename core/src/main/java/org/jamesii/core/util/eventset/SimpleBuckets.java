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
 * The SimpleBuckets is an event queue implementation which uses a HashMap for
 * storing per time buckets of events internally. This queue should be efficient
 * iff the number of different time stamps is rather low and requeue ops happen
 * only quite seldomly while the overall number of events is rather high and all
 * events having the same time stamp are retrieved at once.
 * 
 * <br>
 * O(n) - requeue(E, Double, Double)<br>
 * O(n) - dequeue(E)<br>
 * O(n) - dequeueAllHashed<br>
 * O(1) - dequeueAll(Double)<br>
 * O(n) - getTime<br>
 * O(1) - isEmpty <br>
 * O(1) - size<br>
 * 
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
 * <td>{@link #requeue(Object, Double)}</td>
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
 * 
 * 
 * @author Jan Himmelspach *
 * @param <E>
 */
public class SimpleBuckets<E> extends AbstractEventQueue<E, Double> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 6815838204625838530L;

  /**
   * The per time stamp buckets.
   */
  private Map<Double, List<E>> events = new HashMap<>();

  /**
   * Counter for the number of elements stored in the queue
   */
  private int qsize;

  /**
   * Default constructor.
   * 
   */
  public SimpleBuckets() {
    super();
  }

  @Override
  public Entry<E, Double> dequeue() {
    Double min = getMin();
    List<E> list = events.get(min);

    if ((list != null) && (list.size() != 0)) {

      Entry<E, Double> r = new Entry<>(list.get(0), min);

      qsize--;

      // if only one entry witht his time stamp left throw away the bucket,
      // otherwise delete the dequeued element
      if (list.size() == 1) {
        events.remove(min);
      } else {
        list.remove(0);
      }

      return r;
    }
    if (list != null) {
      events.remove(min);
    }

    return null;
  }

  @Override
  public Double dequeue(E event) {

    Iterator<Map.Entry<Double, List<E>>> it = events.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry<Double, List<E>> e = it.next();
      List<E> list = e.getValue();
      Iterator<E> it2 = list.iterator();
      while (it2.hasNext()) {
        if (it2.next() == event) {
          Double d = e.getKey();
          it2.remove();
          if (list.size() == 0) {
            // System.out.println("no more models with tonie "+d);
            it.remove();
            // System.out.println("events size "+events.size()+" --- q size
            // "+qsize);
          }
          qsize--;
          return d;
        }
      }
    }
    // System.out.println("dequeuing of "+event+" failed");
    return null;
  }

  @Override
  public List<E> dequeueAll() {
    return dequeueAll(getMin());
  }

  @Override
  public List<E> dequeueAll(Double time) {
    List<E> result = events.get(time);
    if (result == null) {
      return new ArrayList<>();
    }
    events.remove(time);
    qsize -= result.size();
    return result;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    return dequeueAllHashed(getMin());
  }

  /**
   * This method returns a list of all events for the given time
   * 
   * @param time
   * @return hashed list of events with the given time stamp
   */
  public Map<E, Object> dequeueAllHashed(Double time) {
    List<E> list = events.get(time);

    Map<E, Object> result = new HashMap<>();

    if (list == null) {
      return result;
    }

    for (E m : list) {
      result.put(m, null);
    }

    // remove the dequeued items!
    qsize -= list.size();
    events.remove(time);

    return result;
  }

  @Override
  public void enqueue(E event, Double time) {
    if (time < 0) {
      return; // invalid value, not to be used here
    }
    // System.out.println ("Adding "+time+" "+event);
    List<E> list = events.get(time);
    if (list == null) {
      list = new ArrayList<>();
      events.put(time, list);
    }
    qsize++;
    list.add(event);
  }

  @Override
  public Double getMin() {

    Double min = null;

    Iterator<Double> it = events.keySet().iterator();

    if (it.hasNext()) {
      min = it.next();
    }

    while (it.hasNext()) {
      Double d = it.next();
      if (Double.compare(min, d) > 0) {
        min = d;
      }
    }

    // System.out.println("got min: "+min);

    return min;
  }

  @Override
  public Double getTime(E event) {

    Iterator<Double> it = events.keySet().iterator();

    while (it.hasNext()) {
      Double d = it.next();
      List<E> list = events.get(d);
      for (E m : list) {
        if (m == event) {
          // System.out.println("time for event "+event+" is "+d);
          return d;
        }
      }
    }

    return null;
  }

  @Override
  public boolean isEmpty() {
    return events.isEmpty();
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
    return qsize;
  }

}
