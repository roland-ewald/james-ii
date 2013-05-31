/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.editable.IEditor;
import org.jamesii.gui.utils.parameters.editor.plugintype.AbstractParamEditorFactory;
import org.jamesii.gui.utils.parameters.editor.plugintype.ParamEditorFactory;

/**
 * Factory for {@link DBConnectionData} editor.
 * 
 * @author Gabriel Blum
 */
public class DBConnectionDataEditorFactory extends ParamEditorFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6769237793587867553L;

  @Override
  public IEditor<?> create(ParameterBlock params) {
    return new DBConnectionDataEditor();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (DBConnectionData.class.isAssignableFrom((Class<?>) params
        .getSubBlockValue(AbstractParamEditorFactory.TYPE))) {
      return 2;
    }
    return 0;
  }

}