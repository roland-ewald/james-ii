/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

/**
 * @author Arne Bittig
 * @date 10.02.2013
 */
public class TreeAndHashMapEventQueueTest extends EventQueueTest {

  /**
   * @param name
   */
  public TreeAndHashMapEventQueueTest(String name) {
    super(name);
  }

  @Override
  public IEventQueue<Object, Double> create() {
    return new TreeAndHashMapEventQueue<>();
  }
}
