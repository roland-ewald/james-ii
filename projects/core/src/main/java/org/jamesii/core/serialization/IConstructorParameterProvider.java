/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.serialization;

/**
 * The Interface IConstructorParameterProvider.
 * 
 * @author Roland Ewald
 */
public interface IConstructorParameterProvider<F> {

  Object[] getParameters(F oldInstance);

}
