/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.util.collection.CollectionUtils;

/**
 * Base class for event queue implementation that use an event->time map for
 * fast {@link #getTime(Object) lookup} (and, by extension, the possibility of
 * faster removal of specific elements not at the head of the queue).
 *
 * @author Arne Bittig
 * @param <E>
 *          Event type
 * @param <T>
 *          Time stamp type
 * @date 11.02.2013
 */
public abstract class AbstractMapEventQueue<E, T extends Comparable<T>>
implements IEventQueue<E, T> {

  private static final long serialVersionUID = 3721620517560856849L;

  private final Map<E, T> evTimeMap;

  /**
   * Constructor taking the event->time map to use (may be based on equality or
   * identity)
   *
   * @param evTimeMap
   *          Event->Time map
   */
  protected AbstractMapEventQueue(Map<E, T> evTimeMap) {
    this.evTimeMap = evTimeMap;
  }

  protected final Map<E, T> getEventTimeMap() {
    return evTimeMap;
  }

  @Override
  public Map<E, Object> dequeueAllHashed() {
    return CollectionUtils
        .fillMap(new HashMap<E, Object>(), dequeueAll(), null);
  }

  @Override
  public final T getTime(E event) {
    return evTimeMap.get(event);
  }

  @Override
  public final void requeue(E event, T newTime) {
    dequeue(event);
    enqueue(event, newTime);
  }

  @Override
  public final void requeue(E event, T oldTime, T newTime) {
    requeue(event, newTime); // no use using oldTime to save calculations as
    // method needs to work even when oldTime is
    // wrong (as it is in one test method).
  }

  @Override
  public final int size() {
    return evTimeMap.size();
  }

  @Override
  public final void setSize(long size) { /* no effect */
  }

  @Override
  public final boolean isEmpty() {
    return evTimeMap.isEmpty();
  }

}
