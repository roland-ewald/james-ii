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
 * @author Arne Bittig
 * @date 08.02.2013
 */
// @Plugin(description = "Event queue using HashMap and inverse TreeMap")
public class TreeAndHashMapEventQueueFactory extends EventQueueFactory {

  private static final long serialVersionUID = -1226225087815273859L;

  /** Default constructor */
  public TreeAndHashMapEventQueueFactory() {
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new TreeAndHashMapEventQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return Math.PI / 4.;
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
