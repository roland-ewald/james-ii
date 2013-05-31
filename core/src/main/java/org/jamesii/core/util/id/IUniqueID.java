/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.id;

import java.io.Serializable;

/**
 * The Interface IUniqueID.
 */
public interface IUniqueID extends Comparable<IUniqueID>, Serializable {

  /**
   * As string.
   * 
   * @return the string
   */
  String asString();

}
