/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.calendar;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.collection.list.SortedList;
import org.jamesii.core.util.eventset.AbstractEventQueue;
import org.jamesii.core.util.eventset.Entry;

/**
 * The CalendarQueue implementation is based on the article "Calendar queues: A
 * fast O(1) priority queue implementation for the simulation event set problem"
 * Brown, R; 1988; Communications of the ACM 31, 10 Oct. 1220-1227 Most event
 * set problem related paper use this queue implementation as base for their
 * considerations.
 * 
 * This queue implementation divides the overall time range into so called
 * years. A year is realized as a list of days/buckets whereby each bucket
 * contains a fixed sub-range (e.g. a aday) of the year time range (e.g. a
 * "year" may 4 time units and bucket contains 1 time unit - thus we require 4
 * buckets). If a events has to be scheduled over a year in advance it is added
 * to the corresponding day bucket, thus being at time 2 an entry with time
 * stamp 7 will be added to bucket no 3. Thus on accessing elements only those
 * are allowed to be returned which have a time stamp of the current year. The
 * number of buckets shall be increased/decreased according to the number of
 * events to be stored in it. If this number doubles the list size is doubled
 * and vice versa.
 * 
 * The principle ideas behind this mechanism are
 * <ul>
 * <li>restrict the search space (n - buckets for m events instead of directly
 * searching in m events) [old here]</li>
 * <li>avoid the overflow list problem, thus do not have a list in which
 * overflow items are stored, re-use the existing buckets instead (maybe this
 * could be even improved if the same scheme is applied more than once, thus
 * dividing a bucket up, according to the years / current year - remainder) [new
 * here]</li>
 * <li>dynamically adopt the number of buckets and their range (while resizing)</li>
 * </ul>
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
 * IDEA: organize buckets in hashmaps -> thus fast remove/add!!!
 * 
 * <p>
 * To be checked: sometimes the queue might not be ok after a resize happens
 * (the least element is no longer retrieved because it is stored in bucket 0!)
 * so it should not be a problem to use this queue for constant setups. This
 * might be due to a problem of increasing time stamp order - new events have to
 * be scheduled with a time >= the last one dequeued!
 * </p>
 * 
 * @author Jan Himmelspach
 * @author University of Rostock
 * @param <M>
 *          The event type to be stored in the list (e.g. IModel)
 * @param <S>
 *          The bucket list to be used (must be sorted)
 */
public class CalendarQueue<M> extends AbstractEventQueue<M, Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3174997486320091471L;

  /** The bot_treshold. */
  private int botThreshold;

  /**
   * The bucket based storage / a list of buckets each containing a "day"
   */
  private List<SortedList<Entry<M, Double>>> bucket =
      new ArrayList<>();

  /**
   * used to determine whether an entry is for the current year
   */
  private double buckettop;

  /** The firstsub. */
  private int firstsub;

  /**
   * Map for storing entries with time stamp infinity (they cannot be stored in
   * the normal cal queue)
   */
  private  Map<M, Double> infEntries = new HashMap<>();

  /**
   * bucket number the last event has been taken from
   */
  private int lastbucket;

  /** The lastprio. */
  private double lastprio;

  /**
   * Number of available buckets (length of bucket array)
   */
  private int nbuckets;

  /**
   * Number of elements in the queue
   */
  private int qsize;

  /**
   * Internally used flag for indicating whether enqueue / dequeue ops can
   * trigger a resize operation or not.
   */
  private boolean resizeenabled = true;

  /**
   * If the queue size (qsize) exceeds this value the queue is resized
   */
  private int topThreshold;

  /**
   * Bucket width (time range to be stored in one bucket - e.g. 0.5)
   */
  private double width;

  /**
   * Instantiates a new calendar queue.
   */
  public CalendarQueue() {
    this(2);
  }

  /**
   * The Constructor.
   * 
   * @param initialsize
   *          the initialsize
   */
  public CalendarQueue(int initialsize) {
    super();
    localinit(0, initialsize, 1.0, 0.0);
    // System.out.println("Simulation results might be wrong due to a bug in the
    // calendar queue implementation");
  }

  @Override
  public Entry<M, Double> dequeue() {

    if (qsize == 0) {
      if (infEntries.size() > 0) {
        Iterator<Map.Entry<M, Double>> it = infEntries.entrySet().iterator();
        Map.Entry<M, Double> me = it.next();
        it.remove();
        return new Entry<>(me.getKey(), me.getValue());
      }
    }

    if (lastbucket > nbuckets) {
      System.out.println("lastbucket is larger as the number of buckets, ups.");
    }
    if (lastbucket == nbuckets) {
      System.out.println("we are in the last bucket");
    }

    int i;

    Entry<M, Double> e = null;

    if (qsize == 0) {
      return null;
    }

    // System.out.println("lastbucket " + lastbucket);
    for (i = lastbucket;;) {
      /* Search buckets */
      /* Check bucket i */
      SortedList<Entry<M, Double>> buck = bucket.get(i);

      // if (buck != null && !buck.isEmpty())
      // System.out.println(buck.top().getTime() + " -- " + buckettop);

      if (buck != null && !buck.isEmpty() && buck.top().getTime() < buckettop) {
        /* Item to dequeue has been found. */
        e = buck.extractTop();

        // Remove item from list;
        /* Update position on calendar. */
        lastbucket = i;
        lastprio = e.getTime(); // priority of item removed;
        --qsize;
        /* Halve calendar size if needed. */
        if (qsize < botThreshold) {
          resize(nbuckets / 2);
        }
        return e; // item found:
      }

      // else
      /*
       * Prepare to check next bucket or else go to a direct search.
       */
      ++i;
      if (i == nbuckets) {
        // nbuckets then this fails!!
        i = 0;
      }
      buckettop += width;
      if (i == lastbucket) {
        break; /* Go to direct search */
      }

    }
    /* Directly search for minimum priority event. */

    lastprio = Double.POSITIVE_INFINITY;

    // System.out.println("searching");
    for (i = 0; i < bucket.size(); i++) {
      SortedList<Entry<M, Double>> ts = bucket.get(i);
      if ((ts != null) && (!ts.isEmpty())) {
        Entry<M, Double> ent = ts.top();
        // System.out.println("ent time"+ent.getTime());
        if (ent.getTime().compareTo(lastprio) == -1) {
          lastprio = ent.getTime();
          lastbucket = i;
          buckettop = width + lastprio; // no min prio found in the current year
          // - let's compute the new years end
        }
      }

    }
    // System.out.println("Search ended with: "+lastprio+" "+lastbucket+"
    // "+buckettop+" width "+width);

    return dequeue(); /* Resume search at minnode. */

  }

  @Override
  public Double dequeue(M event) {

    if (infEntries.remove(event) == null) {
      for (SortedList<Entry<M, Double>> list : bucket) {
        if (list != null) {
          Iterator<Entry<M, Double>> eit = list.iterator();
          while (eit.hasNext()) {
            Entry<M, Double> e = eit.next();
            if (e.getEvent() == event) {
              eit.remove();
              qsize--;
              return e.getTime();
            }
          }
        }
      }
    } else {
      return Double.POSITIVE_INFINITY;
    }
    return null;
  }

  /**
   * Remove an event with a given time stamp. If the event with the time stamp
   * does not exist the method will not raise any error!
   * 
   * @param event
   *          the event
   * @param oldTime
   *          the time of the event as it is currently stored in the queue
   */
  protected void dequeue(M event, Double oldTime) {
    // find virtual bucket
    int i = (int) (oldTime / width);

    // find actual bucket
    i = i % nbuckets;

    SortedList<Entry<M, Double>> buck = bucket.get(i);

    // boolean found = false;

    if (buck != null) {
      Iterator<Entry<M, Double>> it = buck.iterator();
      while (it.hasNext()) {
        Entry<M, Double> e = it.next();
        if ((e.getEvent() == event) && (e.getTime().compareTo(oldTime) == 0)) {
          /* if found remove it */
          it.remove();
          qsize--;
          /*
           * we will not perform any resize op -> new element will be enqueued
           * soon
           */
          // found = true;
          break;
        }
      }
    }

  }

  @Override
  public ArrayList<M> dequeueAll() {

    if (qsize == 0) {
      ArrayList<M> res = new ArrayList<>(infEntries.keySet());
      infEntries.clear();
      return res;
    }

    ArrayList<M> result = new ArrayList<>();

    Entry<M, Double> firstOne = dequeue();

    double b_lastprio = lastprio;

    Entry<M, Double> e = firstOne;

    while ((e != null) && (e.getTime().compareTo(firstOne.getTime()) == 0)) {
      result.add(e.getEvent());
      e = dequeue();
    }

    lastprio = b_lastprio;

    if (e != null) {
      enqueue(e.getEvent(), e.getTime());
    }

    return result;
  }

  @Override
  public List<M> dequeueAll(Double time) {

    if (time == Double.POSITIVE_INFINITY) {
      ArrayList<M> res = new ArrayList<>(infEntries.keySet());
      infEntries.clear();
      return res;
    }

    int i;
    // calc the number of the bucket in which to place the entry

    // find virtual bucket
    i = (int) (time / width);

    // find actual bucket
    i = i % nbuckets;

    SortedList<Entry<M, Double>> buck = bucket.get(i);

    if (buck == null) {
      return null;
    }

    List<Entry<M, Double>> list = buck.getList();

    // TODO we could use binary search in the list for getting the start index
    // and break the loop as soon as we reach a higher value!!!

    List<M> result = new ArrayList<>();

    Iterator<Entry<M, Double>> it = list.iterator();

    while (it.hasNext()) {
      Entry<M, Double> e = it.next();
      if (e.getTime().compareTo(time) == 0) {
        result.add(e.getEvent());
        // System.out.println(e.getTime()+"---"+e.getEvent());
        it.remove();
        qsize--;
      }
    }
    return result;
  }

  @Override
  public Map<M, Object> dequeueAllHashed() {
    Map<M, Object> result = new HashMap<>();

    if (qsize == 0) {
      for (M k : infEntries.keySet()) {
        result.put(k, null);
      }
      infEntries.clear();
      return result;
    }

    ArrayList<M> res = dequeueAll();

    for (int i = 0; i < res.size(); i++) {
      result.put(res.get(i), null);
    }
    // result.put(e.getEvent(), null);
    //
    // Entry<M, Double> firstOne = dequeue();
    //
    // result.put(firstOne.getEvent(), null);
    //
    // // compute the bucket this event is in, take out all other elements with
    // // this time stamp
    //
    // // System.out.println("time: "+firstOne.getTime());
    //
    // // printInfo();
    //
    // // find virtual bucket
    // int i = new Double(firstOne.getTime() / width).intValue();
    //
    // // find actual bucket
    // i = i % nbuckets;
    //
    // SortedList<Entry<M, Double>> buck = bucket.get(i);
    //
    // if (buck == null)
    // return result;
    //
    // Iterator<Entry<M, Double>> it = buck.iterator();
    //
    // while (it.hasNext()) {
    // Entry<M, Double> e = it.next();
    // if (e.getTime().compareTo(firstOne.getTime()) == 0) {
    // result.put(e.getEvent(), null);
    // it.remove();
    // qsize--;
    // }
    // }

    return result;
  }

  @Override
  public void enqueue(M event, Double priority) {

    // System.out.println("enq "+event);

    // if (infEntries.containsKey(event)) {
    // System.out.println("oops "+event+" exists in inf entries");
    // }

    // entries with infinity as time cannot be stored in the calendar queue
    if (priority == Double.POSITIVE_INFINITY) {
      infEntries.put(event, priority);
      return;
    }

    /*
     * if (priority < lastprio) { System.out.println("this should never happen
     * prio < lastprio "+priority+" - "+lastprio); }
     */

    int i;
    // calc the number of the bucket in which to place the entry

    // if (!resizeenabled)
    // System.out.println("priority: " + priority + " width +" + width);

    // find virtual bucket
    i = (int) (priority / width);

    // System.out.println("inserted priority: " + priority + " buck +" + i + "
    // nbuckets "+nbuckets);

    // find actual bucket
    i = i % nbuckets;

    // System.out.println("Take bucket "+i);

    SortedList<Entry<M, Double>> buck = bucket.get(i);
    if (buck == null) {
      buck = new SortedList<>();
      bucket.set(i, buck);
    }

    insert(new Entry<>(event, priority), buck);

    // print(System.out);

    qsize++;

    if (qsize > topThreshold) {
      // System.out.println("have to resize : " + qsize);
      resize(2 * nbuckets);
    }

  }

  /**
   * Gets the bucket string.
   * 
   * @param subList
   *          the sub list
   * 
   * @return the bucket string
   */
  private String getBucketString(SortedList<Entry<M, Double>> subList) {
    if (subList == null) {
      return "";
    }
//    String result = "";
    StringBuilder stringBuilder = new StringBuilder();
    for (Entry<M, Double> m : subList.getList()) {
      stringBuilder.append(m.getTime());
      stringBuilder.append(" --- ");
      stringBuilder.append(m.getEvent());
      stringBuilder.append(" ### ");
//      result += stringBuilder.toString();
    }
    return stringBuilder.toString();//result;
  }

  /**
   * Get the list element stored in a bucket.
   * 
   * @param list
   * @return
   */
  protected Entry<M, Double> getLeast(ArrayList<Entry<M, Double>> list) {
    Double min = Double.POSITIVE_INFINITY;
    Entry<M, Double> result = null;
    for (Entry<M, Double> e : list) {
      if (min.compareTo(e.getTime()) > 0) {
        min = e.getTime();
        result = e;
      }
    }
    return result;
  }

  @Override
  public Double getMin() {

    // print(System.out);

    // printInfo();

    Double t = Double.POSITIVE_INFINITY;
    // int i = -1;
    // int c = 0;
    boolean b = false;
    for (SortedList<Entry<M, Double>> al : this.bucket) {
      if ((al != null) && (!al.isEmpty())) {
        b = true;
        if (al.top().getTime().compareTo(t) < 0) {
          // i = c;
          t = al.top().getTime();
        }
      }
      // c++;
    }
    if ((t.compareTo(Double.POSITIVE_INFINITY) == 0) && (!b)) {
      if (infEntries.size() == 0) {
        return null;
      }
    }
    return t;

    // double b_lastprio = lastprio;
    // resizeenabled = false;
    // Entry<M, Double> e = dequeue();
    // if (e != null) {
    // enqueue(e.getEvent(), e.getTime());
    // resizeenabled = true;
    // lastprio = b_lastprio;
    // return e.getTime();
    // } else {
    // resizeenabled = true;
    // return Double.POSITIVE_INFINITY;
    // }

  }

  // **************************** bucket ops

  /**
   * Gets the string.
   * 
   * @return the string
   */
  public String getString() {
    String result = "";
    for (SortedList<Entry<M, Double>> subList : bucket) {
      result += getBucketString(subList) + "\n";
    }
    return result;
  }

  /**
   * Search for the time of the given event. This op has the complexity of O(n)
   * and it is not part of the original calendar queue implementation.
   * 
   * @param event
   * @return
   */
  @Override
  public Double getTime(M event) {
    for (int i = 0; i < nbuckets; i++) {
      SortedList<Entry<M, Double>> buck = bucket.get(i);
      if (buck != null) {
        for (Entry<M, Double> e : buck) {
          if (e.getEvent() == event) {
            return e.getTime();
          }
        }
      }
    }
    Double result = infEntries.get(event);

    return result;
  }

  /**
   * Initialize the queue. Use for base initialization: 0 as qbase, 2 as number
   * of buckets, 1.0 as bucket width and 0.0 as start priority
   */
  public void initqueue() {
    localinit(0, 2, 1.0, 0.0);
    resizeenabled = true;
  }

  /**
   * Insert entry into the given bucket.
   * 
   * @param e
   * @param nearFuture
   */
  protected void insert(Entry<M, Double> e,
      SortedList<Entry<M, Double>> intoBucket) {
    // System.out.println("bucket size before ins "+e+" --- "+bucket.size()+"
    // overall qsize "+qsize);
    /*
     * if (bucket.getList().contains(e)) throw new RuntimeException("element
     * "+e+" already in!");
     */
    intoBucket.add(e);
    // System.out.println("bucket size after ins "+e+" --- "+bucket.size());
  }

  @Override
  public boolean isEmpty() {
    return qsize == 0;
  }

  /**
   * Localinit.
   * 
   * @param qbase
   *          the qbase
   * @param nbuck
   *          the nbuck
   * @param bwidth
   *          the bwidth
   * @param startprio
   *          the startprio
   */
  public void localinit(int qbase, int nbuck, double bwidth, double startprio) {

    int n;
    // set position and size of new queue
    firstsub = qbase;
    width = bwidth;
    nbuckets = nbuck;
    // TODO calculate bit mask for modulo nbuckets operation

    // initialize as empty

    if (bucket == null) {
      bucket = new ArrayList<>(nbuckets);
    }

    qsize = 0;
    for (int i = 0; i < nbuckets; ++i) {
      bucket.add(i, null);
    }
    // set up initial position in queue
    lastprio = startprio;
    // virtual bucket
    // System.out.println("startprio:" + startprio);
    // System.out.println("widht:" + width);
    n = (int) (startprio / width);
    // System.out.println("n:" + n);
    lastbucket = n % nbuckets;

    buckettop = (n + 1) * width + 0.5 * width;
    // set up queue size change tresholds
    botThreshold = nbuckets / 2 - 2;
    topThreshold = 2 * nbuckets;
  }

  /**
   * This method computes the new width of a bucket in the queue. The width of a
   * bucket is the time range to be stored in it. E.g. 0.5 means that, if the
   * least value is 0.0, that all values in the intervals [0.0, 0.5[, [0.5,
   * 1.0[, ... are stored in separate buckets.
   * 
   * @return width of a bucket
   */
  double newwidth() {
    int nsamples;

    // System.out.println("In newwidth ...");

    if (qsize < 2) {
      return 1.0;
    }

    if (qsize <= 5) {
      nsamples = qsize;
    } else {
      nsamples = 5 + qsize / 10;
    }

    if (nsamples > 25) {
      nsamples = 25;
    }

    double average = 0.0;

    // backup these values
    double b_lastprio = lastprio;
    int b_lastbucket = lastbucket;
    double b_buckettop = buckettop;

    resizeenabled = false;

    ArrayList<Entry<M, Double>> list = new ArrayList<>();

    // dequeue nsample elements
    for (int i = 1; i < nsamples; i++) {
      list.add(dequeue());
    }

    double[] prios = new double[nsamples - 1];

    // enqueue nsample elements
    for (int i = 1; i < nsamples; i++) {
      Entry<M, Double> l = list.get(i - 1);
      Double prio = l.getTime();
      // enqueue nsample elements
      enqueue(l.getEvent(), prio);
      prios[i - 1] = prio;
      average = average + prio;
    }

    average = average / nsamples;

    /*
     * double std = StandardDeviation.standardDeviation(prios) 2;
     * 
     * double useaverage = 0; int useaverage_count = 1;
     * 
     * for (int i = 1; i < nsamples; i++) { Entry<M, Double> l = list.get(i -
     * 1); Double prio = l.getTime(); // TODO reread publication < or >!! if
     * (prio - average > std) { useaverage += prio; useaverage_count++; } }
     * 
     * useaverage = useaverage / useaverage_count;
     */

    if (Double.compare(average, 0.0) == 0) {
      average = 1.0;
    }

    resizeenabled = true;

    // restore backuped values
    lastprio = b_lastprio;
    lastbucket = b_lastbucket;
    buckettop = b_buckettop;
    // System.out.println("Leaving newwidth ...");

    return 3.0 * average;
  }

  /**
   * Prints the queue (content) to the out stream.
   * 
   * @param out
   *          the out
   */
  public void print(PrintStream out) {
    int i = 0;
    for (SortedList<Entry<M, Double>> subList : bucket) {
      out.print((i++) + ": ");
      printBucket(subList, out);
      out.println();
    }
    for (Map.Entry<M, Double> m : infEntries.entrySet()) {
      out.print(m.getValue() + " --- " + m.getKey() + " ### ");
    }
  }

  /**
   * Prints the bucket.
   * 
   * @param subList
   *          the sub list
   * @param out
   *          the out
   */
  private void printBucket(SortedList<Entry<M, Double>> subList, PrintStream out) {
    out.print(getBucketString(subList));
  }

  /**
   * For debugging!
   * 
   */
  public void printInfo() {
    System.out.println("CalendarQueue - debug info ");
    print(System.out);
    System.out.println("lastprio " + lastprio);
    System.out.println("lastbucket " + lastbucket);
    System.out.println("width " + width);
    System.out.println("nbuckets " + nbuckets);
    System.out.println("buckettop " + buckettop);
    System.out.println("qsize " + qsize);
    System.out.println("firstsub " + firstsub);
    System.out.println("top_treshold " + topThreshold);
    System.out.println("bot_treshold " + botThreshold);
  }

  @Override
  public void requeue(M event, Double newTime) {
    /*
     * Here we have to search for the given event and to replace the entry This
     * method is not part of the original implementation of the calendar queue.
     * Here it's complexity is in O (n) + O (enqueue \resize)
     */

    requeueIt(event, newTime);
  }

  @Override
  public void requeue(M event, Double oldTime, Double newTime) {
    /* search for the event */

    if (infEntries.remove(event) != null) {
      enqueue(event, newTime);
      return;
    }

    if (oldTime != null) {
      dequeue(event, oldTime);

      /**
       * enqueue the new element
       */
      enqueue(event, newTime);
    } else {
      // fallback
      requeueIt(event, newTime);
    }

    /*
     * if (!found) throw new EventDoesNotExistException("The event " + event + "
     * with time " + oldTime + " does not exist in the queue!");
     */

  }

  /**
   * Here we have to search for the given event and to replace the entry. This
   * method is not part of the original implementation of the calendar queue.
   * Here it's complexity is in O (n) + O (enqueue \resize).
   * 
   * @param event
   * @param newTime
   */
  protected void requeueIt(M event, Double newTime) {

    /* search for the event */
    // if (infEntries.keySet().contains(event)) {
    // System.out.println("the "+event+" is in the inf entries");
    // }
    boolean found = (infEntries.remove(event) != null);

    for (int i = 0; (i < nbuckets) && !found; i++) {
      // System.out.println("trying to remove "+event+" from the "+i+".th bucket
      // out of "+nbuckets);
      SortedList<Entry<M, Double>> buck = bucket.get(i);

      if (buck != null) {
        int r = -1;
        for (int j = 0; (j < buck.size()) && !found; j++) {
          // Iterator<Entry<M, Double>> it = buck.iterator();
          // while (it.hasNext()) {
          Entry<M, Double> e = buck.get(j);// it.next();
          if (e.getEvent() == event) {
            found = true;
            r = j;
          }
        }
        if (found) {
          /* if found remove it */
          buck.remove(r);
          // System.out.println("bs pre remove "+event+" "+buck.size());
          // printBucket (buck, System.out);
          // if (buck.get(r).getEvent() != event)
          // System.out.println("WRONG ELEMENT TO BE DELETED");
          // System.out.println("removed: "+buck.remove(r));
          // System.out.println("bs post remove "+buck.size());
          // printBucket(buck, System.out);
          qsize--;
          // for (int j = 0; (j < buck.size()); j++) {
          // // Iterator<Entry<M, Double>> it = buck.iterator();
          // // while (it.hasNext()) {
          // Entry<M, Double> e = buck.get(j);// it.next();
          // if (e.getEvent() == event) {
          // System.out
          // .println("NICE FEATURE
          // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! deleted
          // the wrong element");
          // }
          // }
          // System.out.println("buck size reduced");
          /*
           * we will not perform any resize op -> new element will be enqueued
           * soon
           */
          // found = true;
          break;
        }
      }
    }

    // if (!found) System.out.println("was not able to find entry for "+event);

    /*
     * if (!found) throw new EventDoesNotExistException("The event " + event + "
     * does not exist in the queue!");
     */

    /**
     * enqueue the new element
     */
    enqueue(event, newTime);
  }

  /**
   * Resize the bucket array.
   * 
   * @param newsize
   *          number of buckets to be used from now on
   */
  protected void resize(int newsize) {

    // System.out.println("resize current eles:"+qsize+" new size:"+newsize);

    double bwidth;
    // int i;

    List<SortedList<Entry<M, Double>>> oldbucket;
    if (!resizeenabled) {
      return;
    }

    // find new bucket width
    bwidth = newwidth();
    // save location and size of old calendar for use when copying calendar
    oldbucket = bucket;
    bucket = null;

    // initialize new calendar

    // TODO in the original solution the queue is placed in an array of QSPACE
    // slots, from which newsize are uesed!
    /*
     * if (firstsub == 0) { localinit(QSPACE - newsize, newsize, bwidth,
     * lastprio); } else { localinit(0, newsize, bwidth, lastprio); }
     */

    localinit(0, newsize, bwidth, lastprio);
    // copy queue elements to new calendar

    /*
     * for (i = 0; i < oldnbuckets; i++) { SortedList<Entry<M, Double>> list =
     * oldbucket.get(i); if (list != null) for (Entry<M, Double> e : list) {
     * enqueue(e.getEvent(), e.getTime()); srsize++; } }
     */
    resizeenabled = false;
    for (SortedList<Entry<M, Double>> list : oldbucket) {
      // System.out.println("Copy from list "+list);
      if (list != null) {
        for (Entry<M, Double> e : list.getList()) {
          enqueue(e.getEvent(), e.getTime());
        }
      }

      /*
       * for (i = 0; i < list.getList().size(); i++) { Entry<M, Double> e =
       * list.getList().get(i); enqueue(e.getEvent(), e.getTime()); }
       */

    }
    resizeenabled = true;
  }

  @Override
  public void setSize(long size) {
    resize((int) size);
  }

  @Override
  public int size() {
    return qsize + infEntries.size();
  }

}
