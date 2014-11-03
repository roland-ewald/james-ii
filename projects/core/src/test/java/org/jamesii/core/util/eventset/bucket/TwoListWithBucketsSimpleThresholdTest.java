/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.bucket;

import org.jamesii.core.util.eventset.EventQueueTest;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.bucket.TwoListWithBucketsSimpleThreshold;

/**
 * Tests the {@link TwoListWithBucketsSimpleThreshold} event queue.
 * 
 * @author Roland Ewald
 * 
 *         Date: 27.04.2007
 */
public class TwoListWithBucketsSimpleThresholdTest extends EventQueueTest {

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   */
  public TwoListWithBucketsSimpleThresholdTest(String name) {
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
    return new TwoListWithBucketsSimpleThreshold<>();
  }

}
