/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

/**
 * This exception will be thrown if a type inconsistency has been detected. This
 * might happen if a factory of a pre specified type is expected (e.g., during
 * loading) but a different type of factory is loaded.
 * 
 * @author Jan Himmelspach
 * 
 */
public class FactoryTypeException extends RuntimeException {

  /**
   * The constant serial version UID.
   */
  private static final long serialVersionUID = 2083627957131924114L;

  public FactoryTypeException() {
  }

  public FactoryTypeException(String message) {
    super(message);
  }

  public FactoryTypeException(Throwable cause) {
    super(cause);
  }

  public FactoryTypeException(String message, Throwable cause) {
    super(message, cause);
  }

}
