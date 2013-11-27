/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.plugintype;

import java.util.Iterator;

import org.jamesii.core.util.eventset.IEventQueue;

/**
 * Describes how an {@link IEventQueue} implementation compares events, and
 * therefore which events are regarded as identical.
 * 
 * These underlying assumptions are important for the use of
 * {@link IEventQueue#enqueue(Object, Comparable)} versus
 * {@link IEventQueue#requeue(Object, Comparable)}.
 * 
 * @author Arne Bittig
 * 
 */
public enum EventIdentityBehavior {

  /** Event objects are always compared via {@link Object#equals(Object)}. */
  EQUALITY,

  /** Event objects are always compared via their references ('=='). */
  IDENTITY,

  /**
   * Event objects are inconsistently compared. This should be avoided.
   */
  INCONSISTENT;

  /**
   * Logical "and": If something has this behavior in one respect and that other
   * behavior in another respect, the overall behavior is...
   * 
   * @param other
   *          other behavior
   * @return overall behavior
   */
  public EventIdentityBehavior and(EventIdentityBehavior other) {
    if (this.equals(other)) {
      return this;
    } else {
      return INCONSISTENT;
    }
  }

  /**
   * Logical "and" of several different behaviors
   * 
   * @param b
   *          behaviors
   * @return overall behavior
   */
  public static EventIdentityBehavior and(Iterable<EventIdentityBehavior> b) {
    boolean first = true;
    EventIdentityBehavior res = null;
    Iterator<EventIdentityBehavior> it = b.iterator();
    while (it.hasNext()) {
      if (first) {
        res = it.next();
        first = false;
      } else {
        res = res.and(it.next());
      }
    }
    return res;
  }

}
