/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.security.auth.simpleauthmanager;

import java.io.File;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.security.auth.AuthManagerFactory;
import org.jamesii.core.security.auth.IAuthManager;

/**
 * This factory creates {@link SimpleAuthManager}s. See there for details.
 * 
 * @author Simon Bartels
 * 
 */
public class SimpleAuthManagerFactory extends AuthManagerFactory {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = -4997578037635395335L;

  @Override
  public IAuthManager create(ParameterBlock parameters, Context context) {
    return new SimpleAuthManager();
  }

  @Override
  public boolean supportsConfigurationFile(File f) {
    return true;
  }

  /**
   * The quality of this manager factory is Integer.MIN_VALUE.
   */
  @Override
  public int getQualityFeedback() {
    return Integer.MIN_VALUE;
  }

}
