/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * An interface for factories creating objects of type I.
 * 
 * @author Jan Himmelspach
 * @param <I>
 *          Type of produced instances
 */
public interface IFactory<I> {

  /**
   * Creates an object of the type this factory has been created for.
   * 
   * @param parameters
   *          the parameters
   * @return the created instance
   */
  I create(ParameterBlock parameters);

}
