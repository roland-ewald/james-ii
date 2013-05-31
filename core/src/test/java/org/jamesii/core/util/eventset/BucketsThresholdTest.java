/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.util.eventset.BucketsThreshold;
import org.jamesii.core.util.eventset.IEventQueue;

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
    // setDebug(true);
    super.testDequeue();

    // long seed = 3405978569185666048l;
    // addParameter("seedBreakBuckets", seed);
    // setRandom(new JavaRandom(seed));
    //
    // super.testDequeue();
  }

  /**
   * Print the current queue using the toString method.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void print() {
    if (isDebug()) {
      System.out.println(((BucketsThreshold<Object>) getCurrent())
          .toString(true));
    }
  }

}
