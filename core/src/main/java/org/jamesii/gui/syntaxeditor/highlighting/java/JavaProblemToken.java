/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting.java;

import org.jamesii.gui.syntaxeditor.DefaultLexerToken;

/**
 * Problem token type for Java syntax highlighting
 * 
 * @author Stefan Rybacki
 */
public class JavaProblemToken extends DefaultLexerToken<JavaProblemToken.Type> {
  /**
   * Enumeration of problem types. There are two problem types. {@link #WARNING}
   * and {@link #ERROR}
   * 
   * @author Stefan Rybacki
   */
  public static enum Type {
    /**
     * describes the found problem as warning
     */
    WARNING, /**
     * describes the found problem as error
     */
    ERROR
  };

  /**
   * Creates a new problem token for Java syntax highlighting
   * 
   * @param type
   *          the problem type
   * @param start
   *          the token start position
   * @param length
   *          the token's length
   */
  public JavaProblemToken(Type type, int start, int length) {
    super(type, start, length);
  }
}
