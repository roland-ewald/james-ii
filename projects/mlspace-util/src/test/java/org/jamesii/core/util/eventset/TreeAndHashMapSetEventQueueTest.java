/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

/**
 * Test TreeAndHashMapSetEventQueue.
 *
 * @author Arne Bittig
 * @date 13.11.2013
 */
public class TreeAndHashMapSetEventQueueTest extends EventQueueTest {

  /**
   * @param name
   */
  public TreeAndHashMapSetEventQueueTest(String name) {
    super(name);
  }

  @Override
  public IEventQueue<Object, Double> create() {
    return new TreeAndHashMapSetEventQueue<>();
  }
}
