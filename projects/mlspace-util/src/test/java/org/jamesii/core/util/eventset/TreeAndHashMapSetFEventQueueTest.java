/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

/**
 * Test TreeAndHashMapSetFEventQueue.
 *
 * Note: Test is successful as of creation date, but this may be due to a not
 * sufficiently complex test. (Equality/identity behavior may be inconsistent,
 * which may cause size problems)
 *
 * @author Arne Bittig
 * @date 13.11.2013
 */
public class TreeAndHashMapSetFEventQueueTest extends EventQueueTest {

  /**
   * @param name
   */
  public TreeAndHashMapSetFEventQueueTest(String name) {
    super(name);
  }

  @Override
  public IEventQueue<Object, Double> create() {
    return new TreeAndHashMapSetFEventQueue<>();
  }
}
