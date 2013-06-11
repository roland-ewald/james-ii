/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.SimpleBuckets;

/**
 * Tests the {@link SimpleBuckets} event queue.
 * 
 * @author Roland Ewald
 * 
 *         27.04.2007
 */
public class SimpleBucketsTest extends EventQueueTest {

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   */
  public SimpleBucketsTest(String name) {
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
    return new SimpleBuckets<>();
  }
}
