/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

/**
 * @author Jan Himmelspach
 * 
 */
public class TextProcessingException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -580637516154577368L;

  /**
   * 
   */
  public TextProcessingException() {

  }

  /**
   * @param message
   */
  public TextProcessingException(String message) {
    super(message);

  }

  /**
   * @param cause
   */
  public TextProcessingException(Throwable cause) {
    super(cause);

  }

  /**
   * @param message
   * @param cause
   */
  public TextProcessingException(String message, Throwable cause) {
    super(message, cause);

  }

}
