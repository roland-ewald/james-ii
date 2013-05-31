/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.util.eventset.HeapEventQueue;
import org.jamesii.core.util.eventset.IEventQueue;

/**
 * Tests the {@link HeapEventQueue}.
 * 
 * @author Roland Ewald
 * 
 *         Date: 27.04.2007
 */
public class HeapEventQueueTest extends EventQueueTest {

  /**
   * Instantiates a new heap event queue test.
   * 
   * @param name
   *          the name
   */
  public HeapEventQueueTest(String name) {
    super(name);
  }

  /**
   * Creates the queue.
   * 
   * @return the i event queue< object>
   * 
   * @see org.jamesii.core.util.eventset.EventQueueTest#create()
   */
  @Override
  public IEventQueue<Object, Double> create() {
    return new HeapEventQueue<>();
  }

}
