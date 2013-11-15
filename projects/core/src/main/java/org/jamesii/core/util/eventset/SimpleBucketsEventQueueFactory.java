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
 * A factory for creating SimpleBucketsEventQueue objects.
 */
public class SimpleBucketsEventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5827955583789721321L;

  /**
   * Instantiates a new simple buckets event queue factory.
   */
  public SimpleBucketsEventQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new SimpleBucketsEventQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.1;
  }
}
