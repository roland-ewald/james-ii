/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.listener.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for observer listeners.
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractObserverListenerFactory extends
    AbstractFilteringFactory<ObserverListenerFactory> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Class of the existing observer, to which can be listened.
   */
  public static final String OBSERVER_CLASS = "observerClass";

}
