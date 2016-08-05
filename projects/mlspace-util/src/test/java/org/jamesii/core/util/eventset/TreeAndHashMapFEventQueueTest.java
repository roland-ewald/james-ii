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
public class TreeAndHashMapFEventQueueTest extends EventQueueTest {

  /**
   * @param name
   */
  public TreeAndHashMapFEventQueueTest(String name) {
    super(name);
  }

  @Override
  public IEventQueue<Object, Double> create() {
    return new TreeAndHashMapFEventQueue<>();
  }
}
