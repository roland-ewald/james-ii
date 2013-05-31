/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.controller;

import java.util.Set;

import org.jamesii.core.remote.hostcentral.IObjectId;

/**
 * 
 * The interface IObjectReferrer.
 * 
 * @author Simon Bartels
 * 
 */
public interface IObjectReferrer {

  /**
   * Gets the communication partners from.
   * 
   * @param o
   *          The ID of the object of which partners we're interested in.
   * 
   * @return the communication partners from
   * 
   *         A Set of IDs from objects o communicates with.
   */
  Set<IObjectId> getCommunicationPartnersFrom(IObjectId o);

}
