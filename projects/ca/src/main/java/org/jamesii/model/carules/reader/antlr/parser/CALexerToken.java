/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import org.jamesii.gui.syntaxeditor.DefaultLexerToken;

/**
 * Token used
 * 
 * @author Stefan Rybacki
 * 
 */
public class CALexerToken extends DefaultLexerToken<Integer> {

  /**
   * @param type
   * @param start
   * @param length
   */
  public CALexerToken(Integer type, int start, int length) {
    super(type, start, length);
  }

}
