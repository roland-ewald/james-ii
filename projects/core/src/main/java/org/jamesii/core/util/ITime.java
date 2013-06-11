/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

/**
 * The Interface ITime. Can be implemented by all those parts of the system
 * which can return a time value. This can be, e.g., the current time, a start
 * time or which time ever.
 * 
 * @author Jan Himmelspach
 * @param <T>
 */
public interface ITime<T extends Comparable<T>> {

  /**
   * Gets the current (e.g. simulation) "time". As time value any comparable
   * type can be used in principal.
   * 
   * @return the time
   */
  T getTime();

}
