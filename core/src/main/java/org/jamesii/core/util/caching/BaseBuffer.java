/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.caching;

/**
 * Very simple abstract class which just registers itself as a listener for low
 * memory notifications.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class BaseBuffer implements ILowMemoryListener {

  /**
   * Simple buffer.
   */
  public BaseBuffer() {
    super();
    MemoryObserver.INSTANCE.register(this);
  }

}
