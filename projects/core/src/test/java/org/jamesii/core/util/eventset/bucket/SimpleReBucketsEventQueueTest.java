/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.bucket;

import org.jamesii.core.util.eventset.EventQueueTest;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.bucket.SimpleReBucketsEventQueue;


/**
 * Tests the {@link SimpleReBucketsEventQueue} event queue.
 * 
 * @author Roland Ewald
 * 
 *         Date: 27.04.2007
 */
public class SimpleReBucketsEventQueueTest extends EventQueueTest {

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   */
  public SimpleReBucketsEventQueueTest(String name) {
    super(name);
  }

  @Override
  public IEventQueue<Object, Double> create() {
    return new SimpleReBucketsEventQueue<>();
  }

}
