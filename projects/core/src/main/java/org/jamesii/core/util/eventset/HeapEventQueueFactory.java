/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;

/**
 * A factory for creating HeapEventQueue objects.
 */
public class HeapEventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1200898357234371363L;

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new HeapEventQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.5; // guessed!!!
  }

}
