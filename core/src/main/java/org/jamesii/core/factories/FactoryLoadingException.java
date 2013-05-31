/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

/**
 * This exception will be thrown if on loading a factory something went wrong.
 * An example: the factory cannot be read from the location it is supposed to be
 * at.
 * 
 * @author Jan Himmelspach
 * 
 */
public class FactoryLoadingException extends RuntimeException {

  /**
   * The constant serial version UID.
   */
  private static final long serialVersionUID = -7581130903218519925L;

  public FactoryLoadingException() {
    super();
  }

  public FactoryLoadingException(String message) {
    super(message);
  }

  public FactoryLoadingException(Throwable cause) {
    super(cause);
  }

  public FactoryLoadingException(String message, Throwable cause) {
    super(message, cause);
  }

}
