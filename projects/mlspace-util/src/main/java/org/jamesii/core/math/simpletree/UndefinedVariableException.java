/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree;

import java.util.Map;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public class UndefinedVariableException extends RuntimeException {

  private static final long serialVersionUID = -844942023701123596L;

  public UndefinedVariableException(String varName, Map<String, ?> environment) {
    super(varName + " is not a variable in " + environment);
  }
}
