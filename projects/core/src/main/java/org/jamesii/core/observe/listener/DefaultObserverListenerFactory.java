/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.listener;

import org.jamesii.core.factories.Context;
import org.jamesii.core.observe.listener.plugintype.ObserverListenerFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * This factory creates a default observer listener, which can be applied to all
 * kinds of notifying observers. (see
 * {@link org.jamesii.core.observe.INotifyingObserver})
 * 
 * @author Roland Ewald
 * 
 */
public class DefaultObserverListenerFactory extends ObserverListenerFactory {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 1L;

  @Override
  public IObserverListener create(ParameterBlock parameter, Context context) {
    return new DefaultObserverListener();
  }

  @Override
  public String getDescription() {
    return "Default observer listener (for debugging purposes).";
  }

  @Override
  public String getName() {
    return "Minimal Observer Listener";
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    // Supports all observers
    return 1;
  }

}
