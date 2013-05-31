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
 * A factory for creating SimpleEventQueue objects.
 */
public class SimpleEventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4698125892176109926L;

  /**
   * Instantiates a new simple event queue factory.
   */
  public SimpleEventQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new SimpleEventQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0;
  }

}
