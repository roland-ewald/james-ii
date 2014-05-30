/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services.management;

/**
 * The Enum InfoType. Used for indicating the type of change which leads to an
 * activation of any potentially attached observers.
 * 
 * @author Jan Himmelspach
 */
public enum InfoType {

  /** The register. */
  REGISTER,

  /** The unregister. */
  UNREGISTER

}
