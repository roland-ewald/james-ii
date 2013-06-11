/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.dummyobjects;

import java.io.Serializable;

/**
 * This class is just used for testing the remote communication center.
 * 
 * @author Simon Bartels
 * 
 */
public class Dummy implements Serializable {

  /**
   * The serial version id.
   */
  private static final long serialVersionUID = 8599323487130731494L;

  /**
   * The only method thats callable.
   */
  public static final String ONLY_METHOD = "callMe";

  public static final String RESULT = "Dummy: Yeah, I've been called.";

  /**
   * Just a test method that'll be called remote.
   * 
   * @return the public constant RESULT
   */
  public String callMe() {
    System.out.println(RESULT);
    return RESULT;
  }

}
