/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.bucket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.eventset.Entry;

/**
 * This event queue implementation uses a limited number of buckets for storing
 * the near future (an indexed list with limited size). The remaining events are
 * stored in separated buckets in a threshold list. Thus this queue is a
 * two-list implementation. If the min shall be extracted and the near future
 * list is empty it gets filled up half by buckets from the threshold list. This
 * queue implementation performs fairly well. Especially if we are quite lucky
 * to have a constantly filled near future list without the need to swap
 * elements from near future to far future or vice versa.
 * 
 * Classification<br/>
 * <table>
 * <tr>
 * <td><b>Property</b></td>
 * <td><b>Value</b></td>
 * </tr>
 * <tr>
 * <td>List type</td>
 * <td>2-tier list</td>
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
 * <td>Param threshold</td>
 * <td>Number of slots in the near future</td>
 * </tr>
 * </table>
 * 
 * @author Jan Himmelspach
 * @param <E>
 *          the type of the events to be stored in the queue
 */
public class BucketsThreshold<E> extends BasicHashedBucketsEventQueue<E> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -4699298828924476304L;

  /** The events. map of nextbucket - and time stamps */
  private final Map<E, Double> events = new HashMap<>();

  /** The maxTimeNearFuture. */
  private double maxTimeNearFuture = 0;

  /** The minTimeFarFuture. */
  private double minTimeFarFuture = Double.POSITIVE_INFINITY;

  /** Size of the threshold. */
  private int threshold = 10;

  /**
   * The number of buckets to be moved from far future to the near future if
   * {@link #nearFuture} runs empty.
   */
  private final int numBucketsToMove = threshold / 2;

  /**
   * The map from event time to (maps of) event in the near future (has a fixed
   * size).
   */
  private final Map<Double, Map<E, Object>> nearFuture = new HashMap<>();

  /**
   * The map from event time to (maps of) events in the far future (has a
   * variable size).
   */
  private final Map<Double, Map<E, Object>> farFuture = new HashMap<>();

  /**
   * Create a new bucket threshold queue using default values.
   */
  public BucketsThreshold() {
  }

  /**
   * Create a new bucket threshold queue. The first list shall contain thresh
   * entries.
   * 
   * @param thresh
   *          Number of entries of the first list.
   */
  public BucketsThreshold(Integer thresh) {
    threshold = thresh;
  }

  @Override
  public Entry<E, Double> dequeue() {

    if (events.size() == 0) {
      return null;
    }

    Double time = getMin();
    Map<E, Object> l = nearFuture.get(time);
    E e = l.keySet().iterator().next();
    dequeue(e);
    return new Entry<>(e, time);
  }

  @Override
  public Double dequeue(E event) {

    Double time = events.get(event);
    events.remove(event);

    Map<E, Object> list = nearFuture.get(time);
    boolean inNearFuture = (list != null);

    if (!inNearFuture) {
      list = farFuture.get(time);
    }

    if (list != null) {
      list.remove(event);
      if (list.size() == 0) {

        if (inNearFuture) {
          nearFuture.remove(time);
        } else {
          farFuture.remove(time);
        }

        // if we have deleted the list which contained the max element we
        // have to search for another one ...
        if (time.compareTo(maxTimeNearFuture) == 0) {
          maxTimeNearFuture = findMax(nearFuture);
        }
      }
    }
    return time;
  }

  @Override
  public List<E> dequeueAll() {
    return dequeueAll(getMin());
  }

  @Override
  public List<E> dequeueAll(Double time) {
    Map<E, Object> result = nearFuture.remove(time);
    if (result == null) {
      result = farFuture.remove(time);
      if (result == null) {
        return new ArrayList<>();
      }
    }
    for (E e : result.keySet()) {
      events.remove(e);
    }
    return new ArrayList<>(result.keySet());
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    return dequeueAllHashed(getMin());
  }

  /**
   * This method returns a list of all nextbucket for the given time or null if
   * there are no nextbucket for the given time stamp.
   * 
   * @param time
   *          the time
   * 
   * @return null if this feature is not supported
   */
  public Map<E, Object> dequeueAllHashed(Double time) {
    Map<E, Object> result = (nearFuture.remove(time));
    if (result == null) {
      return new HashMap<>();
    }
    for (E e : result.keySet()) {
      events.remove(e);
    }
    return new HashMap<>(result);
  }

  /**
   * Adds a new event to the near future.
   * 
   * @param event
   *          the event to be added
   * @param time
   *          the time
   */
  private void addToNearFuture(E event, Double time) {

    // check whether the time is already in the nearFuture queue
    if (hasBucket(time, nearFuture)) {
      // already a bucket for that time stamp, thus simply add it
      putInList(nearFuture, time, event);
    } else {
      // only insert if there is a free slot in the near future
      if (nearFuture.size() < threshold) {
        putInList(nearFuture, time, event);
        if (Double.compare(time, maxTimeNearFuture) > 0) {
          maxTimeNearFuture = time;
        }
      } else {
        // new event is later as latest in near future, insert into far future
        if (Double.compare(time, maxTimeNearFuture) > 0) {
          // remember the new minimum value as threshold time
          minTimeFarFuture = time;
          putInList(farFuture, time, event);
        } else {
          // insert new into near future, but also move one bucket from near
          // future to far future
          moveBucket(nearFuture, maxTimeNearFuture, farFuture);
          minTimeFarFuture = maxTimeNearFuture;
          putInList(nearFuture, time, event);
          maxTimeNearFuture = findMax(nearFuture);
        }
      }
    }
  }

  @Override
  public void enqueue(E event, Double time) {

    if (time < 0) {
      return; // invalid argument
    }

    // only try to insert into a bucket list if the time is not INFINITY
    // thus, shorten the range to be stored into the buckets
    if (time != Double.POSITIVE_INFINITY) {

      // only insert into near future if the new time is less than the minimum
      // threshold
      if (Double.compare(time, minTimeFarFuture) < 0) {
        addToNearFuture(event, time);
      } else { // otherwise insert into far future
        putInList(farFuture, time, event);
      }

    }
    events.put(event, time);
  }

  /**
   * Find a number of minimal values in the given bucket map.
   * 
   * @param theBuckets
   *          the buckets
   * @param mins
   *          the number of minimal values
   * 
   * @return the double[]
   */
  private Double[] findMins(int mins, Map<Double, Map<E, Object>> theBuckets) {
    Double[] min = new Double[mins];

    for (int i = 0; i < mins; i++) {
      min[i] = Double.POSITIVE_INFINITY;
    }

    for (Double d : theBuckets.keySet()) {
      for (int i = 0; i < mins; i++) {
        if (d.compareTo(min[i]) < 0) {
          shift(min, i);
          min[i] = d;
          break;
        }
        if (d.compareTo(min[i]) == 0) {
          break;
        }
      }
    }
    return min;
  }

  @Override
  public Double getMin() {

    Double mintime = null;

    // if there are no more events in the near future we should try to get some
    // from the far future
    if (nearFuture.size() == 0) {

      // find mins from the far future
      Double[] min = findMins(numBucketsToMove + 1, farFuture);

      for (int i = 0; i < min.length - 1; i++) {
        if (min[i] != Double.POSITIVE_INFINITY) {
          moveBucket(farFuture, min[i], nearFuture);
        }
      }

      maxTimeNearFuture = min[min.length - 2];
      minTimeFarFuture = min[min.length - 1];
      mintime = min[0];
    } else {
      mintime = findMin(nearFuture);
    }

    // get out
    if (mintime == Double.POSITIVE_INFINITY && events.isEmpty()) {
        return null;
    }

    internalGetMin(mintime);

    return mintime;
  }

  @Override
  public Double getTime(E model) {

    Double d = events.get(model);

    if (d != null) {
      return d;
    }

    return null;
  }

  @Override
  public boolean isEmpty() {
    return events.isEmpty();
  }

  /**
   * Moves the Bucket with the given time stamp from the source list to the
   * target list. This methods expects that the source bucket is not null!!!
   * 
   * @param source
   *          the source
   * @param sourceTime
   *          the source time
   * @param target
   *          the target
   */
  private void moveBucket(Map<Double, Map<E, Object>> source,
      Double sourceTime, Map<Double, Map<E, Object>> target) {
    target.put(sourceTime, source.get(sourceTime));
    source.remove(sourceTime);
  }

  /**
   * Right shift of array content. The value at breakindex will be doubled!!
   * 
   * @param arr
   *          the array to be shifted
   * @param breakIndex
   *          if is zero the whole array will be shifted
   */
  private void shift(Double[] arr, int breakIndex) {
    System.arraycopy(arr, breakIndex, arr, breakIndex + 1, arr.length - 1 - breakIndex);
  }

  @Override
  public int size() {
    return events.size();
  }

  /**
   * Converts a map to a readable string.
   * 
   * @param map
   * @return
   */
  private static String mapToString(Map<?, ?> map) {
    StringBuilder builder = new StringBuilder();

    for (Map.Entry<?, ?> entry : map.entrySet()) {
      builder.append(entry.getKey());
      builder.append("\t");
      builder.append(entry.getValue());
      builder.append("\n");
    }

    return builder.toString();
  }

  /**
   * Converts a map to a readable string.
   * 
   * @param map
   * @return
   */
  private static String mapToStringSorted(Map<Double, ?> map) {
    StringBuilder builder = new StringBuilder();

    List<Double> vals = new ArrayList<>();

    for (Map.Entry<Double, ?> entry : map.entrySet()) {
      vals.add(entry.getKey());
    }

    Collections.sort(vals);

    for (Double d : vals) {
      builder.append(d);
      builder.append("\t");
      builder.append(map.get(d));
      builder.append("\n");
    }

    return builder.toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append("min in threshold: " + minTimeFarFuture + "\n");
    builder.append("maxTimeNearFuture: " + maxTimeNearFuture + "\n");
    builder.append("nearFuture:\n " + mapToString(nearFuture) + "\n");
    builder.append("farFuture:\n " + mapToString(farFuture) + "\n");

    return builder.toString();
  }

  public String toString(boolean sorted) {
    if (!sorted) {
      return toString();
    }
    StringBuilder builder = new StringBuilder();

    builder.append("min in threshold: " + minTimeFarFuture + "\n");
    builder.append("maxTimeNearFuture: " + maxTimeNearFuture + "\n");
    builder.append("nearFuture:\n " + mapToStringSorted(nearFuture) + "\n");
    builder.append("farFuture:\n " + mapToStringSorted(farFuture) + "\n");

    return builder.toString();
  }
}
