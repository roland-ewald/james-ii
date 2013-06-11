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
 * A factory for creating UnsortedListEventQueue objects.
 * 
 * @author Jan Himmelspach
 */
public class UnsortedListEventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4673531612550489192L;

  /**
   * Instantiates a new simple event queue factory.
   */
  public UnsortedListEventQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new UnsortedListEventQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0;
  }

}
