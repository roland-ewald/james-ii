/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.tooltip;

import org.jamesii.gui.syntaxeditor.DefaultLexerToken;

/**
 * Class that defines problem tokens for styled tooltips
 * 
 * @author Stefan Rybacki
 */
public class StyledToolTipProblemToken extends
    DefaultLexerToken<StyledToolTipProblemToken.Type> {

  /**
   * Creates a problem token for styled tooltip
   * 
   * @param type
   *          what kind of problem
   * @param start
   *          start position of token in text
   * @param length
   *          length of token
   * 
   * @see Type
   */
  public StyledToolTipProblemToken(Type type, int start, int length) {
    super(type, start, length);
  }

  /**
   * the problem types this token can represent
   * 
   * @author Stefan Rybacki
   * 
   */
  public enum Type {
    /**
     * indicates that the token specifies an obsolete token in the current
     * context
     */
    OBSOLETE, /**
     * indicates that the token represents an invalid value
     */
    INVALIDVALUE, /**
     * indicates that the token represents an invalid nested tag
     */
    INVALIDNESTED, /**
     * indicates that the token represents a tag that has no
     * ending tag
     */
    NOENDTAG, /**
     * indicates that the token represents a tag that has no starting
     * tag
     */
    NOSTARTTAG
  }
}
