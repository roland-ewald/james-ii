/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

/**
 * A base class for event queues.
 * 
 * @author Jan Himmelspach
 * @param <E>
 *          the type of the events to be stored in the queue
 * @param <T>
 *          the type of the "time" value
 */
public abstract class AbstractEventQueue<E, T extends Comparable<T>> implements
    IEventQueue<E, T> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2747870849295713574L;

  /**
   * Set the size of the queue. Maybe implemented by queues for being able to
   * set an initial size (which may speed up some operations).
   * 
   * @param size
   *          initial size of the queue
   */
  @Override
  public void setSize(long size) {
    if (size() > size) {
      throw new IllegalArgumentException(
          "Too few slots for the elements already stored in the queue! New size "
              + size + " < " + size() + "!");
    }
  }

}
