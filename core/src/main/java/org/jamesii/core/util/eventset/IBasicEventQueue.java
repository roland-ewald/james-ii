/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.io.Serializable;

/**
 * A basic event queue / list / future event list / ... . This interface
 * provides the methods of an event list as they are described in the
 * literature. Most proposed event lists are proposed based on this set of
 * functionality to be provided efficiently.<br/>
 * For the framework an extended version of this interface exists
 * {@link IEventQueue} which provides more methods - these extended methods are
 * required by a number of computation algorithms. Whenever possible the
 * extended interface should be provided instead of this simple one.<br/>
 * 
 * @author Jan Himmelspach
 * 
 * @param <E>
 *          the type of the event
 * @param <T>
 *          the type of the time, eg., Integer, Double, ... please note that
 *          using a double can lead to errors as two double values could be
 *          considered to be equal although they are not - so you should make
 *          clear that the precision of the time type is sufficient for your
 *          model
 */
public interface IBasicEventQueue<E, T extends Comparable<T>> extends
    Serializable {

  /**
   * Dequeue the event with the smallest time. Also known as extractMin. Returns
   * null if the queue is empty.
   * 
   * @return Entry containing the event and the the time stamp.
   */
  Entry<E, T> dequeue();

  /**
   * Enqueue an event for the given time. Also known as insert and add.
   * 
   * @param event
   *          to be inserted into the queue
   * @param time
   *          of the event to happen
   */
  void enqueue(E event, T time);

  /**
   * Get the minimal time stamp. Does not modify the queue.
   * 
   * @return the current minimal time stamp, or null if list is empty
   */
  T getMin();

  /**
   * Returns true if the queue is empty.
   * 
   * @return true if the queue is empty false otherwise
   */
  boolean isEmpty();

  /**
   * Return the number of queued elements.
   * 
   * @return the number of entries in the queue
   */
  int size();

}