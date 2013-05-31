/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.session;

/**
 * A general interface for objects that have to expire after a time. Note that
 * you can test whether an object implements this interface correct by extending
 * {@link TestIExperingImplementation}.
 * 
 * @author Simon Bartels
 * 
 */
public interface IExpiring {

  /**
   * The method is called by the timer task to clarify whether this object is
   * still needed.
   * 
   * @return true if the object is still needed
   */
  boolean hasBeenActive();

  /**
   * May be used by other objects to signal that they would still use this
   * object.
   */
  void activated();

  /**
   * Called if the result of {@link #hasBeenActive()} was false. This method
   * signals the object that it has to discard itself.
   */
  void expire();

}
