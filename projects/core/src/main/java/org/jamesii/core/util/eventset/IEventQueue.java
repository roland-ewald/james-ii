/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.List;
import java.util.Map;

/**
 * Basic (future) event set/queue/list interface, all event sets must implement
 * this interface.<br/>
 * Event queues, named quite differently in literature, are a priority queue
 * data structure. Research on these data structures has not only been done in
 * the realm of discrete event modeling and simulation but therein they have
 * been identified early as a major bottleneck. The classic "hold operation",
 * which is a pair of enqueue and dequeue operations, is the basic access
 * pattern for these data structures per event. Thus, according to the number of
 * events to be processed, both operations are executed. The run time efficiency
 * of their implementations may further on depend on the size of the queue, thus
 * on the number of events to be stored, and, depending on the implementation,
 * on the distribution of the time stamps of the events.<br/>
 * Classic queue implementations have a rather limited sets of operations, e.g.,
 * only {@link #enqueue(Object, Comparable)} and {@link #dequeue()}. This
 * interface here is an extended version of that providing a number of
 * convenience methods which can further on speedup some operations required for
 * computing different discrete event model representations. The most important
 * operation among those is the {@link #requeue(Object, Comparable)} operation
 * which allows to update the time stamp of an event already stored in the
 * queue.
 * 
 * <p/>
 * Event queues may or may not be synchronized! If the event queues are not
 * synchronized and accessed concurrently from different threads you should be
 * aware of the fact that some methods may not work properly. Please check the
 * documentation of the event queue you are going to use in these cases.
 * <p/>
 * There maybe any number of events having the same timestamp, but there is only
 * one timestamp per event!
 * <p/>
 * If methods are not implemented they will throw an exception. However, this
 * should only be done in rare cases as event queues might be automatically
 * selected by the software and thus the computation of simulations might crash
 * due to this. <br/>
 * Depending on the type of T (which can be any comparable according to this
 * interface) special values, e.g., Double.POSITIVE_INFINITY need to be
 * supported. Every queue implementation can restrict T to any concrete type,
 * e.g., double etc ... <br/>
 * This event set interface does not assume a continuous time base! However,
 * some of the queue implementations are restricted to a time base of type
 * Double.
 * 
 * <table>
 * <tr>
 * <td><b>Operation</b></td>
 * <td><b>O-notation</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>{@link #enqueue(Object, Comparable)}</td>
 * <td>O(?)</td>
 * <td>Enqueue an event with the given timestamp</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeue()}</td>
 * <td>O(?)</td>
 * <td>Take out the least event (the event with the minimal "time")</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeueAll()}</td>
 * <td>O(?)</td>
 * <td>Take out all elements with the minimal time (into a
 * {@link java.util.List})</td>
 * </tr>
 * <tr>
 * <td>{@link #requeue(Object, Comparable)}</td>
 * <td>O(?)</td>
 * <td>Requeue the event passed, i.e., update the time value of the event</td>
 * </tr>
 * <tr>
 * <td>{@link #getMin()}</td>
 * <td>O(?)</td>
 * <td>Return the time value (#T) of the event with the least "time"</td>
 * </tr>
 * <tr>
 * <td>{@link #requeue(Object, Comparable, Comparable)}</td>
 * <td>O(?)</td>
 * <td>Requeue the event passed. The implementation might use the old value to
 * speed up the search.</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeue(Object)}</td>
 * <td>O(?)</td>
 * <td>Take out the given event</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeueAllHashed()}</td>
 * <td>O(?)</td>
 * <td>Take out all least elements (into a {@link java.util.Map})</td>
 * </tr>
 * <tr>
 * <td>{@link #dequeueAll(Comparable)}</td>
 * <td>O(?)</td>
 * <td>Take out all elements with a time value which is equal to the one passed
 * (compareTo returns 0)</td>
 * </tr>
 * <tr>
 * <td>{@link #getTime(Object)}</td>
 * <td>O(?)</td>
 * <td>Return the time of the event</td>
 * </tr>
 * <tr>
 * <td>{@link #isEmpty()}</td>
 * <td>O(?)</td>
 * <td>Returns true if the queue is empty</td>
 * </tr>
 * <tr>
 * <td>{@link #size()}</td>
 * <td>O(?)</td>
 * <td>Returns the number of elements in the queue</td>
 * </tr>
 * </table>
 * 
 * 
 * @param <E>
 *          the type of events to be stored in the event set
 * @param <T>
 *          the type of the "time" to be used for sorting the events
 * @author Jan Himmelspach
 */
public interface IEventQueue<E, T extends Comparable<T>> extends
    IBasicEventQueue<E, T> {

  /**
   * Remove the given event and return the event's time stamp.
   * 
   * Returns null if the event is not in the queue.
   * 
   * @param event
   *          the event to be removed from the queue
   * @return the time of this event
   */
  T dequeue(E event);

  /**
   * Dequeue all elements with the smallest time stamp. Will return at least an
   * empty list.
   * 
   * @return a list (never null) containing all least time stamped events
   */
  List<E> dequeueAll();

  /**
   * Dequeue all elements with the given time stamp.
   * 
   * @param time
   *          the time
   * 
   * @return a list (never null) containing all events with this time stamp
   */
  List<E> dequeueAll(T time);

  /**
   * Dequeue all elements with the smallest time stamp. This method returns at
   * least an empty HashMap.
   * 
   * This is a convenience method to provide quick access to the least time
   * stamped events which are more commonly returned by the
   * {@link #dequeueAll()} method. The value of the map entries will not be
   * filled by the queue and is most likely null. You will need to call
   * {@link #getMin()} in before to figure out which time stamp the values
   * returned have. Please make sure that in between no concurrent calls are
   * made to the queue.
   * 
   * @return a HashMap (never null) containing all least time stamped events
   */
  Map<E, Object> dequeueAllHashed();

  /**
   * Gets the time of the given event. Does not modify the queue.
   * 
   * @param event
   *          the event to retrieve the time for
   * @return time stamp of the event or null if the event does not exist
   */
  T getTime(E event);

  /**
   * Searches the given event and updates the time entry. The requeue is not
   * part of the original event set problem but is a highly required feature for
   * the processing of several discrete event systems.<br/>
   * <b>NOTE</b>: requeue takes ONE of the entries of the event and replaces it
   * - if an event is scheduled more than once this can lead to indeterministic
   * behaviour! Also known as update.
   * 
   * If an event to be requeued is no longer in the queue this method will
   * behave like the {@link #enqueue(Object, Comparable)} method.
   * 
   * @param event
   *          The event to be updated
   * @param newTime
   *          The new time to be used for the event
   */
  void requeue(E event, T newTime);

  /**
   * Find the entry with the given oldTime time stamp and change the time stamp
   * to newTime. Also known as update.
   * 
   * See {@link #requeue(Object, Comparable)} for more details.
   * 
   * If an event to be requeued is no longer in the queue this method will
   * behave like the {@link #enqueue(Object, Comparable)} method.
   * 
   * @param event
   *          the event to be looked for
   * @param oldTime
   *          the old time of the event to be looked for, might speed up finding
   *          the event to be deleted in some event set implementations
   * @param newTime
   *          the new time stamp for the event
   */
  void requeue(E event, T oldTime, T newTime);

  /**
   * Set the size of the queue (optional operation). By using this method a
   * queue can be informed about the number of events to be stored.
   * 
   * Queue implementations do not need to support this operation.
   * 
   * @param size
   *          : the number of entries which might be written to the queue
   */
  void setSize(long size);

}
