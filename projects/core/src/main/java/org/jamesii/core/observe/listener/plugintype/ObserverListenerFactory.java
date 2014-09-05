/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.listener.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.observe.listener.IObserverListener;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class for all factories for observer listeners.
 * 
 * @author Roland Ewald
 */
public abstract class ObserverListenerFactory extends
    Factory<IObserverListener> implements IParameterFilterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7830262806361399066L;

  /**
   * Creates a new observer listener.
 * @param parameter
   *          the parameter
 * @return the created observer listener
   */
  @Override
  public abstract IObserverListener create(ParameterBlock parameter, Context context);

  /**
   * Get description of observer listener.
   * 
   * @return description of observer listener
   */
  public abstract String getDescription();

}
