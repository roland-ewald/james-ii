package org.jamesii.core.serialization;

/**
 * The Interface IConstructorParameterProvider.
 * 
 * @author Roland Ewald
 */
public interface IConstructorParameterProvider<F> {

  Object[] getParameters(F oldInstance);

}
