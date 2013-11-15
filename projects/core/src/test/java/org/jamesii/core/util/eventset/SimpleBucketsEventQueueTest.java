/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.SimpleBucketsEventQueue;

/**
 * Tests the {@link SimpleBucketsEventQueue} event queue.
 * 
 * @author Roland Ewald
 * 
 *         27.04.2007
 */
public class SimpleBucketsEventQueueTest extends EventQueueTest {

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   */
  public SimpleBucketsEventQueueTest(String name) {
    super(name);
  }

  @Override
  public IEventQueue<Object, Double> create() {
    return new SimpleBucketsEventQueue<>();
  }
}
