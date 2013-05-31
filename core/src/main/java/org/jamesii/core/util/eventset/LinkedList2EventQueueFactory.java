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
 * A factory for creating LinkedListEventQueue objects.
 * 
 * @author Jan Himmelspach
 */
public class LinkedList2EventQueueFactory extends EventQueueFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7999675040106858548L;

  /**
   * Instantiates a new simple event queue factory.
   */
  public LinkedList2EventQueueFactory() {
    super();
  }

  @Override
  public <E> IEventQueue<E, Double> createDirect(ParameterBlock parameter) {
    return new LinkedList2EventQueue<>();
  }

  @Override
  public double getEfficencyIndex() {
    return 0;
  }

}
