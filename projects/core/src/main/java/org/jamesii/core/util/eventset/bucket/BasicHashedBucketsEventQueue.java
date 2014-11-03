/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.bucket;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.eventset.AbstractEventQueue;

/**
 * The Class BasicHashedBucketsEventQueue.
 * 
 * @param <E>
 */
public abstract class BasicHashedBucketsEventQueue<E> extends
    AbstractEventQueue<E, Double> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3230100638771750798L;

  /**
   * Instantiates a new basic hashed buckets event queue.
   */
  public BasicHashedBucketsEventQueue() {
    super();
  }

  /**
   * Finds the bucket with the maximal time.
   * 
   * @param theList
   *          the the list
   * 
   * @return maximal time found in list
   */
  protected final Double findMax(Map<Double, Map<E, Object>> theList) {
    Double max = 0.0;

    for (Double d : theList.keySet()) {
      if (d.compareTo(max) > 0) {
        max = d;
      }
    }

    return max;
  }

  /**
   * Finds the bucket with the minimal time.
   * 
   * @param theList
   *          the the list
   * 
   * @return minimal time found in list
   */
  protected final Double findMin(Map<Double, Map<E, Object>> theList) {
    Double min = Double.POSITIVE_INFINITY;

    for (Double d : theList.keySet()) {
      if (d.compareTo(min) < 0) {
        min = d;
      }
    }

    return min;
  }

  /**
   * Get a string representation of the bucket (list of model names contained in
   * there).
   * 
   * @param theList
   *          the the list
   * 
   * @return the bucket string
   */
  protected final String getBucketString(Map<E, Object> theList) {
    StringBuilder result = new StringBuilder();
    for (E m : theList.keySet()) {
      if (result.length() > 0) {
        result.append(", ");
        result.append(m);
      } else {
        result.append(m);
      }

    }
    return result.toString();
  }

  /**
   * Checkes whether there is a bucket for the given time in the given list or
   * not.
   * 
   * @param time
   *          the time to look for
   * @param theList
   *          the list to look in
   * 
   * @return true if there is a bucket for the given time, false otherwise
   */
  protected final boolean hasBucket(double time,
      Map<Double, Map<E, Object>> theList) {
    return theList.containsKey(time);
  }

  /**
   * Has to be called after the min has been computed for further processing
   * steps related to returning ALL min events.
   * 
   * @param mintime
   *          the mintime
   */
  public void internalGetMin(Double mintime) {
  }

  /**
   * Print the model names of the given bucket on System.out
   * 
   * @param theList
   *          the the list
   */
  protected final void printBucket(Map<E, Object> theList) {
    System.out.println(getBucketString(theList));
  }

  /**
   * Print the given bucket as it is stored in the map.
   * 
   * @param theList
   *          the the list
   */
  protected final void printList(Map<Double, Map<E, Object>> theList) {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<Double, Map<E, Object>> d : theList.entrySet()) {
      builder.append(d);
      builder.append(": \n");
      builder.append(d);
      builder.append(": \n");
      builder.append(getBucketString(d.getValue()));
      builder.append("\n");
    }
    SimSystem.report(Level.INFO, builder.toString());
  }

  /**
   * Put in list.
   * 
   * @param theList
   *          the the list
   * @param time
   *          the time
   * @param event
   *          the event
   */
  protected final void putInList(Map<Double, Map<E, Object>> theList,
      Double time, E event) {
    Map<E, Object> list = theList.get(time);
    if (list == null) {
      list = new HashMap<>();
      theList.put(time, list);
    }
    list.put(event, null);
  }

  @Override
  public void requeue(E model, Double time) {
    dequeue(model);
    enqueue(model, time);
  }

  @Override
  public void requeue(E model, Double oldTime, Double time) {
    dequeue(model);
    enqueue(model, time);
  }

}
