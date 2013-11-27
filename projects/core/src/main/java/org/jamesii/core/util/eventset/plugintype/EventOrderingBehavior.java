package org.jamesii.core.util.eventset.plugintype;

import org.jamesii.core.util.eventset.IEventQueue;

/**
 * Describes how an {@link IEventQueue} implementation returns events with
 * identical time stamps.
 * 
 * This is important when using methods like
 * {@link IEventQueue#dequeueAll(Comparable)}, as it defines which event order
 * to expect there.
 * 
 * @author Arne Bittig
 * @author Roland Ewald
 * 
 */
public enum EventOrderingBehavior {

  /**
   * First in, first out. This means that the order in which events are enqueued
   * is preserved if their sorting criterion (here, sorting by time stamps)
   * considers them to be equal, i.e., the sorting is stable.
   */
  FIFO(true),

  /**
   * Last in, first out. This means that the order in which events are enqueued
   * is <b>reversed</b>. While not stable (see
   * {@link EventOrderingBehavior#FIFO}), the event ordering is still
   * deterministic.
   */
  LIFO(false),

  /**
   * No assumptions can be made regarding the order in which events with the
   * same time stamp are returned.
   */
  NO_ORDER(false);

  /** Flag to define whether the ordering behavior is stable. */
  private final boolean stable;

  private EventOrderingBehavior(boolean stable) {
    this.stable = stable;
  }

  public boolean isStable() {
    return stable;
  }
}
