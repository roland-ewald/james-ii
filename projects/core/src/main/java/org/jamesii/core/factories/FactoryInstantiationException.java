/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

/**
 * The instantiation exception is thrown if during the instantiation of the
 * factory something went wrong.
 * 
 * @author Jan Himmelspach
 * 
 */
public class FactoryInstantiationException extends RuntimeException {

  /**
   * The constant serial version UID.
   */
  private static final long serialVersionUID = -4842073093573243232L;

  public FactoryInstantiationException(String string) {
    super(string);
  }

  public FactoryInstantiationException(String string, Throwable cause) {
    super(string, cause);
  }

}
