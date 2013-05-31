/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.security.auth;

import java.io.Serializable;
import java.security.Principal;

/**
 * Customized principal interface demanding serializability.
 * 
 * @author Simon Bartels
 * 
 */
public interface IPrincipal extends Serializable, Principal {

}
