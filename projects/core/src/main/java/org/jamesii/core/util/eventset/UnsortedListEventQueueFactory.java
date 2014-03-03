/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.eventset.plugintype.EventIdentityBehavior;
import org.jamesii.core.util.eventset.plugintype.EventOrderingBehavior;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;

/**
 * A factory for the {@link UnsortedListEventQueue}.
 * 
 * @author Jan Himmelspach
 */
public class UnsortedListEventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4673531612550489192L;

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new UnsortedListEventQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.0001;
  }

  @Override
  public EventIdentityBehavior getEventIdentityBehaviour() {
    return EventIdentityBehavior.IDENTITY;
  }

  @Override
  public EventOrderingBehavior getEventOrderingBehaviour() {
    return EventOrderingBehavior.FIFO;
  }
}
