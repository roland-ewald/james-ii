/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.editable.IEditor;
import org.jamesii.gui.utils.parameters.editor.plugintype.ParamEditorFactory;

/**
 * Factory for default editor.
 * 
 * @author Roland Ewald
 */
public class FallbackEditorFactory extends ParamEditorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2862267857777523431L;

  @Override
  public IEditor<?> create(ParameterBlock params, Context context) {
    return new FallbackEditor();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    return 1;
  }

}
