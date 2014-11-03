/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.calendar;


import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.eventset.Entry;

/**
 * The CalendarReQueue is an extension to the CalendarQueue for speeding up the
 * requeue operation as required for the DEVS formalism.
 * 
 * The additional operations in enqueue/dequeue make this queue less performant
 * for use in scenarios without need of the requeue operation!!!!
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
 * <td>no parameters/td>
 * <td></td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jan Himmelspach *
 * @param <M>
 */
public class CalendarReQueue<M> extends CalendarQueue<M> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7958295673185185226L;

  /**
   * In the events list pairs of events and tonies are stored. These can be used
   * for requeing - if the old event has to be located first the old time in
   * there can be used therefore
   */
  private Map<M, Double> events = new IdentityHashMap<>();

  @Override
  public Entry<M, Double> dequeue() {
    Entry<M, Double> result = super.dequeue();
    if (result != null) {
      events.remove(result.getEvent());
    }
    return result;
  }

  @Override
  public Double dequeue(M event) {
    Double t = events.remove(event);
    if (t != null) {
      dequeue(event, t);
    }

    return t;
  }

  @Override
  public ArrayList<M> dequeueAll() {
    ArrayList<M> result = super.dequeueAll();
    for (M e : result) {
      events.remove(e);
    }
    return result;
  }

  @Override
  public List<M> dequeueAll(Double time) {
    List<M> result = super.dequeueAll(time);
    for (M e : result) {
      events.remove(e);
    }
    return result;
  }

  @Override
  public Map<M, Object> dequeueAllHashed() {
    Map<M, Object> result = super.dequeueAllHashed();
    for (M e : result.keySet()) {
      events.remove(e);
    }
    return result;
  }

  @Override
  public void enqueue(M event, Double priority) {
    super.enqueue(event, priority);
    events.put(event, priority);
  }

  @Override
  public Double getTime(M event) {
    return events.get(event);
  }

  @Override
  public void requeue(M event, Double newTime) {

    Double oldTime = events.get(event);
    // get the old time for being able to use the more efficient implementation
    // of the requeue operation (we already know the bucket to search in)
    super.requeue(event, oldTime, newTime);
  }

}
