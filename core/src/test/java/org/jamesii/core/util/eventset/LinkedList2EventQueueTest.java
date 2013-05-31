/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.LinkedList2EventQueue;

/**
 * Tests the {@link LinkedList2EventQueue}.
 * 
 * @author Jan Himmelspach
 * 
 * 
 */
public class LinkedList2EventQueueTest extends EventQueueTest {

  /**
   * Instantiates a new simple event queue test.
   * 
   * @param name
   *          the name
   */
  public LinkedList2EventQueueTest(String name) {
    super(name);
  }

  /**
   * Creates the queue.
   * 
   * @return the event queue
   * 
   * @see org.jamesii.core.util.eventset.EventQueueTest#create()
   */
  @Override
  public IEventQueue<Object, Double> create() {
    return new LinkedList2EventQueue<>();
  }

}
