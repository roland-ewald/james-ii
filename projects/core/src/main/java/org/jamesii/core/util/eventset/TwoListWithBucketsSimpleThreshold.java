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
 * This event queue implementation is a two list event queue implementation. It
 * uses a limited number of buckets for storing events in the first list (nea
 * future). And a map to store the values in the tail (second list; far future).
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
 */
public class TwoListWithBucketsSimpleThreshold<E> extends
    BasicHashedBucketsEventQueue<E> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 694024363454798118L;

  /**
   * The list of buckets. All values smaller the minthreshold will be inserted
   * here. First list.
   */
  private Map<Double, Map<E, Object>> nearFuture = new HashMap<>();

  /** The threshold. Second list. */
  private Map<E, Double> farFuture = new HashMap<>();

  /** The maxbuckettime. */
  private double maxbuckettime = 0.;

  /** The max number of entries in the {@link #nearFuture} map. */
  private int threshold = 8;

  /** The size. */
  private int size = 0;

  /**
   * Create a new simple threshold queue using default values.
   */
  public TwoListWithBucketsSimpleThreshold() {
    super();
  }

  /**
   * Create a new simple threshold queue. The first list shall contain thresh
   * entries.
   * 
   * @param thresh
   *          Number of entries of the first list.
   */
  public TwoListWithBucketsSimpleThreshold(Integer thresh) {
    super();
    threshold = thresh;
  }

  /**
   * Reorganize. Searches for minimum time and moves this to the front. Assumes
   * that buckets is empty!!
   */
  private void reorganize() {
    Double min = Double.POSITIVE_INFINITY;
    Map<E, Object> toCopy = new HashMap<>();

    if (farFuture.isEmpty()) {
      return;
    }

    for (Map.Entry<E, Double> entry : farFuture.entrySet()) {
      int c = min.compareTo(entry.getValue());
      if (c > 0) {
        min = entry.getValue();
        toCopy.clear();
        toCopy.put(entry.getKey(), null);
      } else {
        if (c == 0) {
          toCopy.put(entry.getKey(), null);
        }
      }
    }
    maxbuckettime = min;

    for (E e : toCopy.keySet()) {
      farFuture.remove(e);
    }

    nearFuture.put(min, toCopy);
  }

  /**
   * Min in buckets. Does not modify the internal data structures
   * 
   * @return the double or null if no entry in there
   */
  private Double minInBuckets() {
    Double d = null;

    for (Map.Entry<Double, Map<E, Object>> entry : nearFuture.entrySet()) {
      if (d == null) {
        d = entry.getKey();
      } else {
        if (d.compareTo(entry.getKey()) > 0) {
          d = entry.getKey();
        }
      }
    }
    return d;
  }

  @Override
  public Entry<E, Double> dequeue() {

    Double d = minInBuckets();

    if (d == null) {
      reorganize();

      d = minInBuckets();

      // if d is null there should be no more entries in the queue
      if (d == null) {
        return null;
      }
    }

    // get the map the
    Map<E, Object> l = nearFuture.get(d);

    Iterator<E> it = l.keySet().iterator();
    if (it.hasNext()) {
      E e = it.next();
      it.remove();
      size--;

      // if this bucket is empty we remove it from the queue
      if (!it.hasNext()) {
        nearFuture.remove(d);
      }

      return new Entry<>(e, d);

    }

    return null;
  }

  @Override
  public Double dequeue(E event) {

    Double d = farFuture.remove(event);

    if (d == null) {

      Map.Entry<Double, Map<E, Object>> found = null;

      for (Map.Entry<Double, Map<E, Object>> list : nearFuture.entrySet()) {

        if (list.getValue().containsKey(event)) {
          found = list;
          break;
        }

      }

      if (found != null) {
        found.getValue().remove(event);
        d = found.getKey();

        if (found.getValue().isEmpty()) {
          nearFuture.remove(d);
        }

      }

    }

    if (d != null) {
      size--;
    }
    return d;
  }

  @Override
  public List<E> dequeueAll() {
    List<E> result = dequeueAll(getMin());

    return result;
  }

  @Override
  public List<E> dequeueAll(Double time) {
    Map<E, Object> collection = nearFuture.remove(time);

    List<E> result;

    if (collection == null) {

      result = new ArrayList<>();

      Iterator<Map.Entry<E, Double>> it = farFuture.entrySet().iterator();

      while (it.hasNext()) {

        Map.Entry<E, Double> e = it.next();

        if (e.getValue().compareTo(time) == 0) {
          result.add(e.getKey());
          it.remove();
        }
      }
    } else {
      result = new ArrayList<>(collection.keySet());
    }
    size -= result.size();
    return result;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    return dequeueAllHashed(getMin());
  }

  /**
   * This method returns a list of all bucket for the given time or null if
   * there are no bucket for the given time stamp.
   * 
   * @param time
   *          the time
   * 
   * @return null if this feature is not supported
   */
  public Map<E, Object> dequeueAllHashed(Double time) {
    Map<E, Object> result = nearFuture.remove(time);
    if (result == null) {

      List<E> list = dequeueAll(time);

      result = new HashMap<>();

      for (E e : list) {
        result.put(e, null);
      }

    } else {
      // if the bucket has been directly found and removed we need to decrease
      // the size; dequeueAll does this for the alternative case
      size -= result.size();
    }

    return result;
  }

  @Override
  public void enqueue(E event, Double time) {

    // if (time < 0) {
    // System.err.println("value smaller 0 added for " + event.toString());
    // return; // invalid value, not to be used here
    // }

    // System.out.println ("Adding "+time+" "+event);

    // System.out.print("Inserting "+event.getFullName()+" with time "+time);

    // only try to insert into bucket list if the time is not INFINITY
    // thus, shorten the range to be stored into the bucket list
    if (time != Double.POSITIVE_INFINITY) {

      // only insert into bucket if the new time is less or equal the minimum in
      // the treshold
      if (time.compareTo(maxbuckettime) <= 0) {

        // System.out.print(" it is inserted in the head list ");

        if (hasBucket(time, nearFuture)) {
          // already a bucket for that time stamp, thus simply add it
          // System.out.print(" - inserted in existing head slot ");
          putInList(nearFuture, time, event);
          size++;
          return;
        } else {

          // only insert if there is a free slot in the bucket queue
          if (nearFuture.size() < threshold) {

            // System.out.print (" - head size limit not reached - ");

            putInList(nearFuture, time, event);
            size++;
            return;
          } else {

            Double max = findMax(nearFuture);

            // System.out.print (" - head size limit reached - ");

            if (time.compareTo(max) > 0) {
              // auto insert into threshold, but let's remember the new minimum
              // value
              // in the threshold
              maxbuckettime = max;

            } else {

              // time is smaller than current max time, but no space, thus we
              // have to move the max bucket to the threshold

              Map<E, Object> mod = nearFuture.get(max);

              for (E e : mod.keySet()) {
                farFuture.put(e, max);
              }

              // throw away the bucket
              nearFuture.remove(max);
              // insert new bucket

              mod = new HashMap<>();

              // put the new event into the bucket
              mod.put(event, null);

              // // get all other events having the same time stamp TODO check!!
              // for (E m : events.keySet()) {
              // Double d = events.get(m);
              // if (d.compareTo(time) == 0) {
              // mod.put(m, null);
              // }
              // }

              // add the new bucket to the bucket list
              nearFuture.put(time, mod);
              // new min of trash is current max of bucket
              maxbuckettime = findMax(nearFuture);

              size++;
              return;
            }

          }
        }
      } // else: do nothing (auto insert into threshold)

    }
    size++;
    // System.out.println(" EOI ");
    farFuture.put(event, time);
  }

  @Override
  public Double getMin() {

    Double min = minInBuckets();

    // we found something, let's return it
    if (min != null) {
      return min;
    }

    // nothing in the head, let's check whether the tail is empty
    if (farFuture.isEmpty()) {
      return null;
    }

    // let's search the tail for the min
    min = Double.POSITIVE_INFINITY;
    for (E m : farFuture.keySet()) {
      Double time = farFuture.get(m);
      if (Double.compare(time, min) < 0) {
        min = time;
      }

    }

    return min;
  }

  @Override
  public Double getTime(E event) {

    Double d = farFuture.get(event);

    if (d == null) {

      for (Map.Entry<Double, Map<E, Object>> list : nearFuture.entrySet()) {

        if (list.getValue().containsKey(event)) {

          return list.getKey();
        }

      }

    }

    return d;
  }

  @Override
  public boolean isEmpty() {
    return farFuture.isEmpty();
  }

  /**
   * Shifts entries in the given array one pos right.
   * 
   * @param arr
   *          the arr
   */
  @SuppressWarnings({ "unused" })
  private void shift(Double[] arr) {
    for (int i = arr.length - 1; i > 0; i--) {
      arr[i] = arr[i - 1];
    }
  }

  @Override
  public int size() {
    return size;
  }

}
