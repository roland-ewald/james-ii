/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.bucket;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.plugintype.EventIdentityBehavior;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;

/**
 * A factory for the {@link BucketsThreshold} event queue.
 */
public class BucketsThresholdEventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2741823578072180362L;

  /**
   * Instantiates a new buckets threshold event queue factory.
   */
  public BucketsThresholdEventQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    if (parameter != null) {
      if (parameter.hasSubBlock("threshold")) {
        Integer thresh = parameter.getSubBlock("threshold").getValue();
        return new BucketsThreshold<>(thresh);
      }
    }
    return new BucketsThreshold<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.3;
  }

  @Override
  public EventIdentityBehavior getEventIdentityBehaviour() {
    return EventIdentityBehavior.EQUALITY;
  }

}