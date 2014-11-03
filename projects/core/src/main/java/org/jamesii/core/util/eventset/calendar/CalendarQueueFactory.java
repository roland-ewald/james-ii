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
 * A factory for creating CalendarQueue objects.
 * 
 * @author Jan Himmelspach
 */
public class CalendarQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5502196874023407122L;

  /**
   * Instantiates a new calendar queue factory.
   */
  public CalendarQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new CalendarQueue<>();
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
