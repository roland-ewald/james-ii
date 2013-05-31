/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * This interface allows to easily define a common feature of the abstract
 * factory pattern: the feature that each factory should decide on its own --
 * based on the abstract factory's parameters – if it is applicable to the
 * problem at hand or not.
 * 
 * @author Roland Ewald
 */
public interface IParameterFilterFactory {

  /**
   * Tests whether the type of the given problem (defined by the parameters) is
   * supported by the object(s) this factory is able to instantiate. The larger
   * the returned number, the better suited is the factory.
   * 
   * @param parameters
   *          parameter for the factory
   * @return >0 this kind of setting is supported by the object(s) this factory
   *         can create
   */
  int supportsParameters(ParameterBlock parameters);

}
