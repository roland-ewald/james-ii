/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.security.auth;

import java.io.File;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Template for factories creating authentication and authorization managers.
 * 
 * @author Simon Bartels
 * 
 */
public abstract class AuthManagerFactory extends Factory<IAuthManager> {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = -7579947683176475567L;

  /**
   * Creates a new authentication and authorization manager.
   * 
   * @param parameters
   *          the parameters
   * @return an auth manager
   */
  @Override
  public abstract IAuthManager create(ParameterBlock parameters, Context context);

  /**
   * The return value should be chosen in a way that a client can deduce the
   * data format of the parameter block in
   * {@link IAuthManager#handleLogin(ParameterBlock)}.
   * 
   * @return the authentication description
   */
  public String getAuthenticationDescription() {
    return this.getClass().getName();
  }

  /**
   * Lets the factory decide whether it can handle the given configuration or
   * not.
   * 
   * @param f
   *          the configuration file
   * @return true or false
   */
  public abstract boolean supportsConfigurationFile(File f);

  /**
   * Information about how fast and secure the underlying security system is.
   * The greater the value the better.
   * 
   * @return Something between Integer.MIN_VALUE and Integer.MAX_VALUE
   */
  public abstract int getQualityFeedback();

}
