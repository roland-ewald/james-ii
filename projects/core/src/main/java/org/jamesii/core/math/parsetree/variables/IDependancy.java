/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.variables;

/**
 * Should be implemented by all parse tree nodes which might depend on
 * "external" knowledge.
 * 
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IDependancy {

  /**
   * Depends on values available at time.
   * 
   * @param time
   *          the time
   * 
   * @return true, if depends on
   */
  boolean dependsOn(Integer time);

}
