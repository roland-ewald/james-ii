/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

/**
 * Interface that needs to be implemented by tokens used in the
 * {@link SyntaxEditor}
 * 
 * @see SyntaxEditor
 * @author Stefan Rybacki
 */
public interface ILexerToken extends Comparable<ILexerToken> {
  /**
   * @return the tokens start position in the text and has to be <b>>=0</b>
   */
  int getStart();

  /**
   * @return the tokens length (has to be <b>>0</b>)
   */
  int getLength();

  /**
   * Supposed to return <code>getStart()+getEnd()</code>
   * 
   * @return the end position of this token in the text
   */
  int getEnd();

  /**
   * moves the start position relative to the current start position
   * 
   * @param length
   *          the amount the start position is shifted
   */
  void moveStart(int length);

  /**
   * moves the end position relative to the current end position
   * 
   * @param length
   *          the amount the end position is shifted
   */
  void moveEnd(int length);
}
