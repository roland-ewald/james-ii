/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting.java;

import org.jamesii.gui.syntaxeditor.DefaultLexerToken;

/**
 * Syntax token type for Java syntax highlighting
 * 
 * @author Stefan Rybacki
 */
public class JavaSyntaxToken extends DefaultLexerToken<JavaSyntaxToken.Type> {
  /**
   * Enumeration of syntax token types.
   * 
   * @author Stefan Rybacki
   */
  public static enum Type {
    /**
     * token is an operator
     */
    OPER, /**
     * token is a keyword
     */
    KEYWORD, /**
     * token is an identifier
     */
    IDENT, /**
     * token is a number
     */
    NUMBER, /**
     * token is a string
     */
    STRING, /**
     * token is an escaped string
     */
    STRING_ESC, /**
     * token is a string expression
     */
    GSTRING_EXPR, /**
     * token is a comment
     */
    COMMENT, /**
     * token is a regular expression
     */
    REGEX, /**
     * token is a type
     */
    TYPE, /**
     * token is default token
     */
    DEFAULT
  }

  /**
   * Creates a new syntax tokens for Java syntax highlighting
   * 
   * @param type
   *          the token type
   * @param start
   *          the token start position
   * @param length
   *          the token's length
   */
  public JavaSyntaxToken(Type type, int start, int length) {
    super(type, start, length);
  }
}
