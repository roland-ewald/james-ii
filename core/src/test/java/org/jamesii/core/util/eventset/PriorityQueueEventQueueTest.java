/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.PriorityQueueEventQueue;

/**
 * Tests the {@link PriorityQueueEventQueue}.
 * 
 * @author Jan Himmelspach
 * 
 * 
 */
public class PriorityQueueEventQueueTest extends EventQueueTest {

  /**
   * Instantiates a new simple event queue test.
   * 
   * @param name
   *          the name
   */
  public PriorityQueueEventQueueTest(String name) {
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
    return new PriorityQueueEventQueue<>();
  }

}
