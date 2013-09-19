/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.syntaxeditor.highlighting.IHighlighter;

/**
 * Basic factory for all factories that create model highlighters for the syntax
 * editor
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class HighlightingFactory extends Factory<IHighlighter> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 5167909894284902435L;

  /**
   * Creates highlighter.
   * 
   * @param getValue
   *          opf the parameter block should return the input that should be
   *          highlighted (needed so the factory can decide which highlighter to
   *          return)
   * @return highlighter (null if no highlighter can be provided for given
   *         input)
   */
  @Override
  public abstract IHighlighter create(ParameterBlock input);

  /**
   * Specifies whether the factory can provide a suitable highlighter for the
   * given input object. The given input object can be anything from a file,
   * text or any other object.
   * <p/>
   * If applied for usage in a
   * {@link org.jamesii.gui.model.base.ModelTextEditor} the specified input is
   * an {@link org.jamesii.core.model.symbolic.ISymbolicModel} for which the
   * factory might can provide a {@link IHighlighter} that supports the textual
   * representation (
   * {@link org.jamesii.core.model.symbolic.ISymbolicModel#getAsDocument(Class)}
   * ) of the supplied model.
   * 
   * @param input
   *          the input that might be supported by the factory
   * @return true if factory can provide a highlighter for the given input,
   *         false else
   */
  public abstract boolean supportsInput(Object input);
}
