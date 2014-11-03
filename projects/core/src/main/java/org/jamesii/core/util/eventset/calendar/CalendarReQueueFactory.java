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
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;

/**
 * A factory for creating CalendarReQueue objects.
 * 
 * @author Jan Himmelspach
 */
public class CalendarReQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5502196874023407122L;

  /**
   * Instantiates a new calendar re queue factory.
   */
  public CalendarReQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new CalendarReQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.9;
  }

  @Override
  public EventIdentityBehavior getEventIdentityBehaviour() {
    return EventIdentityBehavior.IDENTITY;
  }

}
