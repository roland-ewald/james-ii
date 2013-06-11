/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.security.auth;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

/**
 * Instances of this interface are responsible for handling the authentication
 * and authorization for a single subject.
 * 
 * @author Simon Bartels
 * 
 */
public interface IAuthManager {

  /**
   * In this method the login procedure has to be handled. (Whether login
   * parameters are handled over in the parameter block or will be fetched in
   * this method is left to the developer.)
   * 
   * @param parameterBlock
   *          login parameters
   * @return true if the authentication was successful
   * @throws Throwable
   *           whatever goes wrong
   */
  boolean handleLogin(ParameterBlock parameterBlock) throws SecurityException;

  /**
   * Checks whether a certain action can be authorized. If everything's fine the
   * method just returns.
   * 
   * @param parameterBlock
   *          contains information which action is about to be made and on which
   *          object
   * @throws Throwable
   *           will be thrown in case authorization can't be granted
   */
  void checkAuthorization(ParameterBlock parameterBlock)
      throws SecurityException;

  /**
   * Returns the principal the security system is using and this manager is
   * responsible for.
   * 
   * @return the principal
   */
  IPrincipal getPrincipal();

  /**
   * If the policy allows to dynamically add users then it is done with this
   * method.
   * 
   * @param parameter
   *          parameter, depends on the implementation
   * @return success and information, say a message or a password
   */
  Pair<Boolean, ParameterBlock> registerUser(ParameterBlock parameter);

}
