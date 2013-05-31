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
 * A factory for creating PriorityQueueEventQueue objects.
 * 
 * @author Jan Himmelspach
 */
public class PriorityQueueEventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4354767181135626608L;

  /**
   * Instantiates a new simple event queue factory.
   */
  public PriorityQueueEventQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new PriorityQueueEventQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0.1;
  }

}
