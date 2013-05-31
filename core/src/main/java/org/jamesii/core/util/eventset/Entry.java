/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.io.Serializable;

/**
 * Events to be used in event set implementations (wherever an event and the
 * time need to be stored together). Each entry holds the "event(s)" to be
 * executed as well as the corresponding time stamp
 * 
 * @param <E>
 *          the type of the event (can for e.g. a special message type)
 * @param <T>
 *          the type of the time
 * @author Jan Himmelspach
 */
public class Entry<E, T extends Comparable<T>> implements Serializable,
    Comparable<Entry<E, T>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6517000416017186516L;

  /** The event as such. */
  private E event;

  /** The time of the event. */
  private T time;

  /**
   * Create a null event with null as time value.
   */
  public Entry() {
    super();
    event = null;
    time = null;
  }

  /**
   * The Constructor.
   * 
   * @param event
   *          the event
   * @param time
   *          the time
   */
  public Entry(E event, T time) {
    super();
    this.event = event;
    this.time = time;
  }

  /**
   * First compares the time stamps, and afterwards the references of the events
   * thus time = time and event = event is enough for equality. <br>
   * <b> Please note that the event is only considered if the time stamps are
   * considered to be equal. If the events are not equal a 1 is returned which
   * means that the obj this element is compared to is larger - this can be used
   * to find the first occurence of "time" in a binary search. This type of
   * comparison is required in the SortedList (remove function!). </b>
   * 
   * @param obj
   *          the object to compare to
   * 
   * @return the int (-1 means this is smaller, 0 means quality, 1 means the obj
   *         is larger)
   */
  @Override
  public int compareTo(Entry<E, T> obj) {
    int result = getTime().compareTo(obj.getTime());
    if (result == 0) {
      if (this.event != obj.getEvent()) {
        result = 1;
      }
    }
    return result;
  }

  /**
   * Gets the event.
   * 
   * @return the event
   */
  public final E getEvent() {
    return event;
  }

  /**
   * Gets the time.
   * 
   * @return the time
   */
  public final T getTime() {
    return time;
  }

  @Override
  public String toString() {
    return event + " - " + time;
  }

}
