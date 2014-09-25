/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.highlighter;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.gui.syntaxeditor.highlighting.IHighlighter;
import org.jamesii.gui.syntaxeditor.highlighting.plugintype.HighlightingFactory;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;

/**
 * A factory for creating CAHighlighter objects.
 */
@Plugin(description = "Highlighter for CA models")
public class CAHighlighterFactory extends HighlightingFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2053711500779068632L;

  /**
   * Creates highlighter.
 * @param input
   *          the input to highlight
 * @return highlighter (null if no highlighter can be provided for given
   *         input)
   */
  @Override
  public IHighlighter create(ParameterBlock input, Context context) {
    return new CAHighlighter();
  }

  @Override
  public boolean supportsInput(Object input) {
    return (input instanceof ISymbolicCAModel);
  }

}
