/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private Map<E, Double> events = new HashMap<>();

  /** The maxevents. */
  private double maxevents = 0;

  /** The minthresholdtime. */
  private double minthresholdtime = Double.POSITIVE_INFINITY;

  /** Size of the threshold. */
  private int threshold = 10;

  /**
   * The movebuckets. number of buckets to be moved from tholdbucket to
   * nearFuture if nearFuture runs empty
   */
  private int movebuckets = threshold / 2;

  /** The nextbucket. map of nextbucket (fixed size) */
  private Map<Double, Map<E, Object>> nearFuture = new HashMap<>();

  /** The farFuture. map of non - next nextbucket (variable size) */
  private Map<Double, Map<E, Object>> farFuture = new HashMap<>();

  /**
   * Create a new bucket threshold queue using default values.
   */
  public BucketsThreshold() {
    super();
  }

  /**
   * Create a new bucket threshold queue. The first list shall contain thresh
   * entries.
   * 
   * @param thresh
   *          Number of entries of the first list.
   */
  public BucketsThreshold(Integer thresh) {
    super();
    threshold = thresh;
  }

  @Override
  public Entry<E, Double> dequeue() {

    if (events.size() == 0) {
      return null;
    }

    Double d = getMin();

    Map<E, Object> l = nearFuture.get(getMin());

    E e = l.keySet().iterator().next();

    dequeue(e);

    return new Entry<>(e, d);
  }

  @Override
  public Double dequeue(E event) {

    boolean ev = true;

    Double d = events.get(event);

    events.remove(event);

    Map<E, Object> list = nearFuture.get(d);

    if (list == null) {
      ev = false;
      list = farFuture.get(d);
    }

    if (list != null) {
      list.remove(event);
      if (list.size() == 0) {
        if (ev) {
          nearFuture.remove(d);
        } else {
          farFuture.remove(d);
        }

        // if we have deleted the list which contained the max element we
        // have to search for another one ...
        if (d.compareTo(maxevents) == 0) {
          maxevents = findMax(nearFuture);

          // System.out.println("Searched for new max element "+maxevents);
        }

      }
    }
    /*
     * if (minthresholdtime < maxevents) { System.out.print("ERR: nextbucket:
     * "); printOrderedList (nextbucket); System.out.print("ERR: tholdbucket:
     * "); printOrderedList (tholdbucket); throw new RuntimeException("REMOVE:
     * oh no ...!!!!"); }
     */
    return d;
  }

  @Override
  public List<E> dequeueAll() {
    return dequeueAll(getMin());
  }

  @Override
  public List<E> dequeueAll(Double time) {
    Map<E, Object> result = (nearFuture.remove(time));
    if (result == null) {
      return new ArrayList<>();
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
    // System.out.println(nextbucket);
    Map<E, Object> result = (nearFuture.remove(time));
    if (result == null) {
      return new HashMap<>();
    }
    for (E e : result.keySet()) {
      events.remove(e);
    }
    // System.out.println("result ("+time+"): "+result);
    return new HashMap<>(result);
  }

  // insert:
  // insert into nextbucket if
  // -nextbucket.size < trashhold and neu < min trashhold
  // -tonie in nextbucket or tonie < max tonie in nextbucket
  // insert into trashhold if
  // -nextbucket.size > trashhold *
  // -tonie > min threshold *
  // -tonie is Double.POSITIVE_INFINITY *

  private void addToNearFuture(E event, Double time) {
    // +++ time < minthresholdtime && time <=> maxevents && nextbucket.size
    // <=> threshold && maxevents < minthresholdtime

    // now it's getting difficult

    // System.out.print("Inserting into head thus "+time+" <
    // "+minthresholdtime);

    // check whether the time is already in the nearFuture queue
    if (hasBucket(time, nearFuture)) {
      // already a bucket for that time stamp, thus simply add it
      // System.out.print(" - inserted in existing head slot ");
      putInList(nearFuture, time, event);
    } else {

      // only insert if there is a free slot in the nextbucket queue
      if (nearFuture.size() < threshold) {

        // +++ time < minthresholdtime && time <=> maxevents &&
        // nextbucket.size
        // < threshold && maxevents < minthresholdtime

        // System.out.print(" - few in head -> insert there ");

        putInList(nearFuture, time, event);

        if (Double.compare(time, maxevents) > 0) {

          // +++ time < minthresholdtime && time > maxevents &&
          // nextbucket.size
          // < threshold && maxevents < minthresholdtime

          // System.out.print(" maxevents set to " + time);
          maxevents = time;
        }

      } else {

        // +++ time < minthresholdtime && time <=> maxevents &&
        // nextbucket.size
        // => threshold && maxevents < minthresholdtime

        // no slot, but minimum in threshold list is greater

        // System.out.print(" - head full -> ");

        // check whether max in nextbucket is less or not
        Double max = maxevents;

        // new is later as latest in nextbucket, insert into threshold
        if (Double.compare(time, max) > 0) {

          // +++ time < minthresholdtime && time > maxevents &&
          // nextbucket.size
          // => threshold && maxevents < minthresholdtime

          // auto insert into threshold, but let's remember the new minimum
          // value
          // in the threshold
          minthresholdtime = time;

          // System.out.print(" insert in tholdbucket + ");

          putInList(farFuture, time, event);

        } else {

          // +++ time < minthresholdtime && time <= maxevents &&
          // nextbucket.size
          // => threshold && maxevents < minthresholdtime

          // insert new into nextbucket, and move one bucket from nextbucket
          // to
          // tholdbucket

          // move the bucket from the nextbucket list into the threshold
          moveBucket(nearFuture, max, farFuture);

          // moveBucket (tholdbucket, time, nextbucket);

          minthresholdtime = max;

          // System.out.print(" make slot free (move) and insert in head ");

          putInList(nearFuture, time, event);
          maxevents = findMax(nearFuture);

        }

      }

    }
  }

  @Override
  public void enqueue(E event, Double time) {

    if (time < 0) {
      return; // invalid value, not to be used here
      // System.out.println ("Adding "+time+" "+event.getFullName());
      // System.out.println ("Var values:\n maxevents="+maxevents+"\n max
      // nextbucket="+findMax(nextbucket)+"\n
      // minthreshold="+minthresholdtime+"\n
      // find min tholdbucket="+findMin(tholdbucket));
    }

    // only try to insert into nextbucket list if the time is not INFINITY
    // thus, shorten the range to be stored into the nextbucket list
    if (time != Double.POSITIVE_INFINITY) {

      // only insert into nextbucket if the new time is less than the minimum in
      // the
      // trashhold
      if (Double.compare(time, minthresholdtime) < 0) {

        addToNearFuture(event, time);

      } else {
        // +++ time > minthresholdtime && time > maxevents && nextbucket.size
        // <=>
        // threshold

        // System.out.print(" insert in tholdbucket ");
        putInList(farFuture, time, event);
      }

    }

    /*
     * System.out.print(""); System.out.println("Inserted (min treshold time
     * "+minthresholdtime+")"); System.out.print("nextbucket: ");
     * printOrderedList (nextbucket); System.out.print("tholdbucket: ");
     * printOrderedList (tholdbucket);
     * 
     * if (findMin (tholdbucket) < findMax (nextbucket)) { throw new
     * RuntimeException("INSERT: Constraint violated ...!!!!"); }
     * 
     * if (minthresholdtime < maxevents) throw new RuntimeException("INSERT: oh
     * no ...!!!!");
     */

    events.put(event, time);
  }

  /**
   * Find the mins min values in the given list.
   * 
   * @param theList
   *          the the list
   * @param mins
   *          the mins
   * 
   * @return the double[]
   */
  private Double[] findMins(int mins, Map<Double, Map<E, Object>> theList) {
    Double[] min = new Double[mins];

    // System.out.println("finding mins");

    for (int i = 0; i < mins; i++) {
      min[i] = Double.POSITIVE_INFINITY;
    }

    for (Double d : theList.keySet()) {
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

    // if there are no more nextbucket we should try to get some from the
    // tholdbucket
    if (nearFuture.size() == 0) {

      /*
       * Double min = findMin(tholdbucket); moveBucket (tholdbucket, min,
       * nextbucket); minthresholdtime = findMin(tholdbucket);
       */

      // find mins from the tholdbucket
      Double[] min = findMins(movebuckets + 1, farFuture);

      for (int i = 0; i < min.length - 1; i++) {
        if (min[i] != Double.POSITIVE_INFINITY) {
          moveBucket(farFuture, min[i], nearFuture);
        }
      }
      // System.out.println("Had to shift");

      maxevents = min[min.length - 2];

      minthresholdtime = min[min.length - 1];

      mintime = min[0];

    } else {

      mintime = findMin(nearFuture);
    }

    // System.out.println("nextbucket:"); printList (nextbucket);
    // System.out.println("tholdbucket:"); printList (tholdbucket);

    // get out
    if (mintime == Double.POSITIVE_INFINITY) {
      if (events.isEmpty()) {
        return null;
      }
    }

    internalGetMin(mintime);

    /*
     * System.out.println("++++++ MIN ++++++"); System.out.println("got min:
     * "+mintime+" ++++ "+this.handleCoupledModels); printList (nextbucket);
     * System.out.println("--- MINBUCK ---"); printBucket (nextbucket, mintime);
     * System.out.println("###### MIN ######");
     */
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
    for (int i = arr.length - 1; i > breakIndex; i--) {
      arr[i] = arr[i - 1];
    }
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

    builder.append("min in threshold: " + minthresholdtime + "\n");
    builder.append("maxevents: " + maxevents + "\n");
    builder.append("nearFuture:\n " + mapToString(nearFuture) + "\n");
    builder.append("farFuture:\n " + mapToString(farFuture) + "\n");

    return builder.toString();
  }

  public String toString(boolean sorted) {
    if (!sorted) {
      return toString();
    }
    StringBuilder builder = new StringBuilder();

    builder.append("min in threshold: " + minthresholdtime + "\n");
    builder.append("maxevents: " + maxevents + "\n");
    builder.append("nearFuture:\n " + mapToStringSorted(nearFuture) + "\n");
    builder.append("farFuture:\n " + mapToStringSorted(farFuture) + "\n");

    return builder.toString();
  }
}
