/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.editable.IEditor;
import org.jamesii.gui.utils.parameters.editor.plugintype.AbstractParamEditorFactory;
import org.jamesii.gui.utils.parameters.editor.plugintype.ParamEditorFactory;

/**
 * Factory for the {@link Integer} editor.
 * 
 * @author Roland Ewald
 */
public class IntegerEditorFactory extends ParamEditorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7737019031905785290L;

  @Override
  public IEditor<?> create(ParameterBlock params) {
    return new NumberEditor<>();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (Integer.class.isAssignableFrom((Class<?>) params
        .getSubBlockValue(AbstractParamEditorFactory.TYPE))) {
      return 5;
    }
    return 0;
  }

}
