/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.caching;

/**
 * Listener interface for low memory events.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface ILowMemoryListener {

  /**
   * This method will be called in case that a low level of free memory has been
   * reached. A memory listener should try to free memory if this method is
   * called.
   */
  void lowMemory();

}
