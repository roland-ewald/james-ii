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
 * A factory for the {@link TwoListWithBucketsSimpleThreshold} event queue.
 */
public class TwoListWithBucketsSimpleThresholdFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6748658802240991820L;

  /**
   * Instantiates a new simple threshold event queue factory.
   */
  public TwoListWithBucketsSimpleThresholdFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    if (parameter != null) {
      if (parameter.hasSubBlock("threshold")) {
        Integer thresh = parameter.getSubBlock("threshold").getValue();
        return new TwoListWithBucketsSimpleThreshold<>(thresh);
      }
    }
    return new TwoListWithBucketsSimpleThreshold<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.2;
  }

  @Override
  public EventIdentityBehavior getEventIdentityBehaviour() {
    return EventIdentityBehavior.EQUALITY;
  }
}
