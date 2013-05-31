/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.SortedListEventQueue;

/**
 * Tests the {@link SortedListEventQueue}.
 * 
 * @author Jan Himmelspach
 * 
 * 
 */
public class SortedListEventQueueTest extends EventQueueTest {

  /**
   * Instantiates a new simple event queue test.
   * 
   * @param name
   *          the name
   */
  public SortedListEventQueueTest(String name) {
    super(name);
  }

  /**
   * Creates the queue.
   * 
   * @return the event queue
   * 
   * @see org.jamesii.core.util.eventset.EventQueueTest#create()
   */
  @Override
  public IEventQueue<Object, Double> create() {
    return new SortedListEventQueue<>();
  }

}
