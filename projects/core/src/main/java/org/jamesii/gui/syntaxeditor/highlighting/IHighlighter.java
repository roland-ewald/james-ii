/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting;

import org.jamesii.core.model.symbolic.convert.IDocument;
import org.jamesii.gui.syntaxeditor.ILexer;
import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;

/**
 * Interface that needs to be implemented by a class that wants to support
 * highlighting of a arbitrary syntax using the
 * {@link org.jamesii.gui.syntaxeditor.SyntaxEditor}.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IHighlighter {

  /**
   * Gets the lexer.
   * 
   * @return the lexer used to scan the model syntax
   */
  ILexer getLexer();

  /**
   * Gets the syntax stylizer.
   * 
   * @return the stylizer that defines the appearance of scanned syntax tokens
   */
  ILexerTokenStylizer<ILexerToken> getSyntaxStylizer();

  /**
   * Gets the name.
   * 
   * @return the name of the highlighter
   */
  String getName();

  /**
   * Gets the document class the highlighter supports.
   * 
   * @return the document class
   */
  Class<? extends IDocument<?>> getDocumentClass();
}
