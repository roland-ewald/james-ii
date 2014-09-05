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
import org.jamesii.gui.utils.parameters.editor.plugintype.AbstractParamEditorFactory;
import org.jamesii.gui.utils.parameters.editor.plugintype.ParamEditorFactory;

/**
 * Factory for string editors.
 * 
 * @author Roland Ewald
 */
public class StringEditorFactory extends ParamEditorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -685765861324212760L;

  @Override
  public IEditor<?> create(ParameterBlock params, Context context) {
    return new StringEditor();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (String.class.isAssignableFrom((Class<?>) params
        .getSubBlockValue(AbstractParamEditorFactory.TYPE))) {
      return 5;
    }
    return 0;
  }

}
