/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.sorting;

/**
 * There are two ways of ordering a sequence monotonously, ascending and
 * descending
 * 
 * @author Roland Ewald
 * 
 *         22.05.2007
 * 
 */
public enum SortOrder {

  /**
   * Ascending: eg., 1,2,3
   */
  ASCENDING,

  /**
   * Descending: eg., 3,2,1
   */
  DESCENDING
}
