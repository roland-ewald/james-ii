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
 * Factory for the {@link Boolean} editor.
 * 
 * @author Roland Ewald
 */
public class BooleanEditorFactory extends ParamEditorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -2524639317081250082L;

  @Override
  public IEditor<?> create(ParameterBlock params, Context context) {
    return new BooleanEditor();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (Boolean.class.isAssignableFrom((Class<?>) params
        .getSubBlockValue(AbstractParamEditorFactory.TYPE))) {
      return 5;
    }
    return 0;
  }

}
