/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.highlighter;

import org.jamesii.core.model.symbolic.convert.IDocument;
import org.jamesii.gui.syntaxeditor.ILexer;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.highlighting.IHighlighter;
import org.jamesii.model.carules.CARulesAntlrDocument;

// TODO: Auto-generated Javadoc
/**
 * The Class CAHighlighter used to provide a highlighter for
 * {@link org.jamesii.gui.model.base.ModelTextEditor} for the CA language
 * described here
 * {@link org.jamesii.model.carules.reader.antlr.parser.CaruleParser}.
 */
public class CAHighlighter implements IHighlighter {

  /**
   * The syntax stylizer.
   */
  private ILexerTokenStylizer syntaxStylizer = new CASyntaxStylizer();

  @Override
  public ILexer getLexer() {
    return new CALexerWrapper();
  }

  @Override
  public ILexerTokenStylizer getSyntaxStylizer() {
    return syntaxStylizer;
  }

  @Override
  public String getName() {
    return "Highlighter for CA models";
  }

  @Override
  public Class<? extends IDocument<?>> getDocumentClass() {
    return CARulesAntlrDocument.class;
  }

}
