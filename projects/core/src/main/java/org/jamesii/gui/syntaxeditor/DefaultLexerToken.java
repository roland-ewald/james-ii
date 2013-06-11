/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import org.jamesii.gui.tooltip.StyledToolTipProblemToken;
import org.jamesii.gui.tooltip.StyledToolTipSyntaxToken;

/**
 * Helper class that can be used for subclassing for implementing custom ILexer
 * tokens that have already a predefined Type class. This class can simply used
 * as Generics parameter and the resulting class only needs a proper constructor
 * and is done.
 * 
 * @author Stefan Rybacki
 * @param <Type>
 *          type parameter specifying the type of the token type (mostly
 *          {@link Integer} or {@link Number} since tokens can be categorized
 *          with numbers)
 * 
 * @see StyledToolTipSyntaxToken
 * @see StyledToolTipProblemToken
 */
public class DefaultLexerToken<Type> implements ILexerToken {
  /**
   * token type (generic parameter)
   */
  private Type type;

  /**
   * start position of token in text
   */
  private int start;

  /**
   * length of token
   */
  private int length;

  /**
   * Creates a basic token specifying its type and start and length
   * 
   * @param type
   *          the token type
   * @param start
   *          the start position
   * @param length
   *          the token's length
   */
  public DefaultLexerToken(Type type, int start, int length) {
    this.type = type;
    this.start = start;
    this.length = length;

    if (length < 0 || start < 0) {
      throw new IllegalArgumentException(
          "Length must be >=0 and start must be >=0 (start: " + start
              + " length: " + length + ")");
    }
  }

  /**
   * @return the token type
   */
  public final Type getType() {
    return type;
  }

  @Override
  public final int getEnd() {
    return start + length;
  }

  @Override
  public final int getLength() {
    return length;
  }

  @Override
  public final int getStart() {
    return start;
  }

  @Override
  public final int compareTo(ILexerToken o) {
    if (o.getStart() < getStart()) {
      return 1;
    }
    if (o.getStart() > getStart()) {
      return -1;
    }
    return 0;
  }

  @Override
  public final void moveStart(int s) {
    this.start += s;
  }

  @Override
  public final void moveEnd(int l) {
    this.length += l;
  }

  @Override
  public String toString() {
    return String.format("start: %d end: %d length: %d type: %s", getStart(),
        getEnd(), getLength(), getType());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DefaultLexerToken) {
      return ((ILexerToken) obj).getStart() == getStart()
          && ((ILexerToken) obj).getLength() == getLength()
          && ((DefaultLexerToken<?>) obj).getType().equals(type);
    }
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return (43 * getEnd() + start * 31 + 13 * length);
  }

}
