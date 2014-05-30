/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.dsplay;

import org.jamesii.core.util.eventset.EventQueueTest;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.dsplay.DsplayEventQueue;

/**
 * @author Arne Bittig
 * @date 18.11.2013
 */
public class DsplayEventQueueTest extends EventQueueTest {

  /**
   * @param name
   */
  public DsplayEventQueueTest(String name) {
    super(name);
  }

  @Override
  public IEventQueue<Object, Double> create() {
    return new DsplayEventQueue<>();
  }

}
