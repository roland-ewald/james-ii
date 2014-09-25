/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import org.jamesii.gui.syntaxeditor.DefaultLexerToken;
import org.jamesii.model.carules.CARulesModel;
import org.jamesii.model.carules.symbolic.SymbolicCAModel;

/**
 * Used by the {@link CaruleParser} to provide information about syntactical and
 * semantic errors in {@link CARulesModel}s defined as {@link SymbolicCAModel}.
 * 
 * @author Stefan Rybacki
 * 
 */
public class CAProblemToken extends DefaultLexerToken<Integer> {
  /**
   * Problem constant: state is already defined.
   */
  public static final int STATE_ALREADY_DEFINED = 1;

  /**
   * Problem constant: syntactical error.
   */
  public static final int RECOGNITION_ERROR = 2;

  /**
   * Problem constant: value out of range.
   */
  public static final int VALUE_OUT_OF_RANGE = 3;

  /**
   * Problem constant: state is not defined.
   */
  public static final int STATE_NOT_DEFINED = 4;

  /**
   * The error message.
   */
  private String message;

  /**
   * The value.
   */
  private String value;

  /**
   * Instantiates a new CA problem token.
   * 
   * @param type
   *          the type of problem
   * @param start
   *          the start position
   * @param length
   *          the length of the token
   * @param message
   *          the error message
   * @param value
   *          the actual text value of this token
   * @see #RECOGNITION_ERROR
   * @see #STATE_ALREADY_DEFINED
   * @see #STATE_NOT_DEFINED
   * @see #VALUE_OUT_OF_RANGE
   */
  public CAProblemToken(Integer type, int start, int length, String message,
      String value) {
    super(type, start, length);
    this.message = message;
    this.value = value;
  }

  @Override
  public String toString() {
    return super.toString() + " message: " + message;
  }

  /**
   * Returns the associates error message if any
   * 
   * @return the error message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @return the actual text value of the token
   */
  public String getValue() {
    return value;
  }
}
