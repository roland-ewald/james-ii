/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services.availability;

import org.jamesii.core.services.IService;

/**
 * The Interface IAvailabilityProspect.
 * 
 * @author Oliver Röwer
 * @param <I>
 * 
 */
public interface IAvailabilityProspect<I extends IService> {

  /**
   * Handles unreachable services.
   * 
   * @param service
   *          the service
   */
  void serviceUnreachable(I service);
}
