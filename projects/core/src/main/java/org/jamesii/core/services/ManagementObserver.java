/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.Observer;

/**
 * An asynchronous update interface for receiving notifications about Management
 * information.
 * 
 * @author Jan Himmelspach
 */
public class ManagementObserver extends Observer {

  /** The serialization ID. */
  private static final long serialVersionUID = -2042355981935224788L;

  /** The registry. */
  private ServiceRegistry registry;

  /**
   * This method is called when information about an Management which was
   * previously requested using an asynchronous interface becomes available.
   * 
   * @param registry
   *          the registry
   */
  public ManagementObserver(ServiceRegistry registry) {
    super();
    this.registry = registry;
  }

  @Override
  public void update(IObservable entity) {
    registry.changed();
  }

  @Override
  public void update(IObservable entity, Object hint) {
    registry.changed(hint);
  }

}
