/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.calendar;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.plugintype.EventIdentityBehavior;
import org.jamesii.core.util.eventset.plugintype.EventOrderingBehavior;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;

/**
 * A factory for creating dynymiac CalendarQueue objects.
 * 
 * @author Jan Himmelspach
 */
public class DynamicCalendarQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3295241358002217265L;

  /**
   * Instantiates a new calendar queue factory.
   */
  public DynamicCalendarQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new DynamicCalendarQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.8;
  }

  @Override
  public EventIdentityBehavior getEventIdentityBehaviour() {
    return EventIdentityBehavior.IDENTITY;
  }

  @Override
  public EventOrderingBehavior getEventOrderingBehaviour() {
    return EventOrderingBehavior.UNORDERED_REPRODUCIBLE;
  }
}
