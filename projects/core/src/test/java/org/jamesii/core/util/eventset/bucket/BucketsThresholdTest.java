/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.bucket;

import org.jamesii.core.util.eventset.EventQueueTest;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.bucket.BucketsThreshold;

/**
 * The Class BucketsThresholdTest.
 * 
 * @author Roland Ewald
 * 
 *         27.04.2007
 */
public class BucketsThresholdTest extends EventQueueTest {

  /**
   * Instantiates a new buckets threshold test.
   * 
   * @param name
   *          the name
   */
  public BucketsThresholdTest(String name) {
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
    return new BucketsThreshold<>();
  }

  @Override
  public void testDequeue() {
    super.testDequeue();
  }

  /**
   * Print the current queue using the toString method.
   */
  @Override
  protected void print() {
    if (isDebug()) {
      System.out.println(((BucketsThreshold<Object>) getEventQueue())
          .toString(true));
    }
  }

}
