/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting;

import org.jamesii.core.model.symbolic.convert.IDocument;
import org.jamesii.core.model.symbolic.convert.SimpleDocument;
import org.jamesii.gui.syntaxeditor.ILexer;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.SyntaxEditor;
import org.jamesii.gui.syntaxeditor.highlighting.java.JavaLexer;
import org.jamesii.gui.syntaxeditor.highlighting.java.JavaSyntaxTokenStylizer;

/**
 * Basic highlighter usable to highlight arbitrary text in {@link SyntaxEditor}.
 * It uses the {@link DefaultLexer} that supports Java highlighting so far.
 * 
 * @author Stefan Rybacki
 */
public class DefaultHighlighter implements IHighlighter {
  /**
   * Supported highlightings
   * 
   * @author Stefan Rybacki
   */
  public static enum Style {
    /**
     * Highlighting for Java like syntax
     */
    JAVA, /**
     * no highlighting
     */
    DEFAULT
  }

  /**
   * the lexer to use
   */
  private ILexer lexer;

  /**
   * the syntax stylizer to use
   */
  private ILexerTokenStylizer syntaxStylizer;

  /**
   * the style that is currently used by the highlighter
   */
  private Style style;

  /**
   * Creates an {@link IHighlighter} for the given {@link Style}
   * 
   * @param style
   *          the style of highlighting
   */
  public DefaultHighlighter(Style style) {
    this.style = style;
    switch (style) {
    case JAVA:
      lexer = new JavaLexer();
      syntaxStylizer = JavaSyntaxTokenStylizer.getInstance();
      break;
    default:
      lexer = DefaultLexer.getInstance();
      syntaxStylizer = new DefaultStylizer();
      break;
    }
  }

  @Override
  public ILexer getLexer() {
    return lexer;
  }

  @Override
  public ILexerTokenStylizer getSyntaxStylizer() {
    return syntaxStylizer;
  }

  @Override
  public String getName() {
    switch (style) {
    case JAVA:
      return "Basic Java Highlighting";
    default:
      return "No Highlighting";
    }
  }

  @Override
  public Class<? extends IDocument<?>> getDocumentClass() {
    return SimpleDocument.class;
  }

}
