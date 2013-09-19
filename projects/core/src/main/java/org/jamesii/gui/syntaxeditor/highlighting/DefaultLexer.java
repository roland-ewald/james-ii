/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.gui.syntaxeditor.ILexer;
import org.jamesii.gui.syntaxeditor.ILexerToken;

/**
 * Lexer used by the
 * {@link org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter.DefaultHighlighter}
 * for the style
 * {@link org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter.Style#DEFAULT}
 * .
 * 
 * @author Stefan Rybacki
 * 
 */
class DefaultLexer implements ILexer {
  /**
   * singleton instance
   */
  private static final DefaultLexer instance = new DefaultLexer();

  /**
   * tokens
   */
  private final List<? extends ILexerToken> tokens = new ArrayList<>();

  /**
   * hidden constructor because of singleton pattern
   */
  private DefaultLexer() {
    // nothing to do
  }

  @Override
  public List<? extends ILexerToken> getSyntaxTokens() {
    return tokens;
  }

  @Override
  public void parse(Reader input) {
    // nothing to do
  }

  @Override
  public void stopParsing() {
    // nothing to do
  }

  /**
   * @return the singleton instance of {@link DefaultLexer}
   */
  public static DefaultLexer getInstance() {
    return instance;
  }
}
