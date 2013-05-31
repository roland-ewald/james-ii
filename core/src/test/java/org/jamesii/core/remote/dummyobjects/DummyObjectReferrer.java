/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.dummyobjects;

import java.util.HashSet;
import java.util.Set;

import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.controller.IObjectReferrer;

/**
 * @author Simon Bartels
 * 
 *         A dummy object referrer which says nobody has any partners.
 */
public class DummyObjectReferrer implements IObjectReferrer {

  @Override
  public Set<IObjectId> getCommunicationPartnersFrom(IObjectId o) {
    // return an empty set
    return new HashSet<>();
  }

}
