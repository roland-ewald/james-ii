/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.security.auth.simpleauthmanager;

import org.jamesii.core.security.auth.IPrincipal;

/**
 * A dummy principal for the {@link SimpleAuthManager}.
 * 
 * @author Simon Bartels
 * 
 */
public class DummyPrincipal implements IPrincipal {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = -2764599299190759891L;

  @Override
  public String getName() {
    return "generic username";
  }

}
