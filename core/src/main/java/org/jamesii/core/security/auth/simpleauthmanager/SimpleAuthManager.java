/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.security.auth.simpleauthmanager;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.security.auth.IAuthManager;
import org.jamesii.core.security.auth.IPrincipal;
import org.jamesii.core.util.misc.Pair;

/**
 * Simple authentication and authorization manager which basically allows
 * everything.
 * 
 * @author Simon Bartels
 * 
 */
public class SimpleAuthManager implements IAuthManager {

  @Override
  public boolean handleLogin(ParameterBlock parameterBlock) {
    return true;
  }

  @Override
  public void checkAuthorization(ParameterBlock parameterBlock) {
    // throwing no exceptions means to allow the action
  }

  @Override
  public IPrincipal getPrincipal() {
    return new DummyPrincipal();
  }

  @Override
  public Pair<Boolean, ParameterBlock> registerUser(ParameterBlock parameter) {
    return new Pair<>(true, null);
  }

}
