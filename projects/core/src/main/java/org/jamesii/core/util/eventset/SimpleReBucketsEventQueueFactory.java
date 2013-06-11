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
 * A factory for creating SimpleReBucketsEventQueue objects.
 */
public class SimpleReBucketsEventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5571754110328730784L;

  /**
   * Instantiates a new simple re buckets event queue factory.
   */
  public SimpleReBucketsEventQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new SimpleReBuckets<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.4;
  }

}
