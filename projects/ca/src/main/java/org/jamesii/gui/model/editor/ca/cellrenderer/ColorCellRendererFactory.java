/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.model.editor.ca.cellrenderer.plugintype.CACellRendererFactory;

/**
 * Factory for the {@link ColorCellRenderer} class.
 * 
 * @author Stefan Rybacki
 */
public class ColorCellRendererFactory extends CACellRendererFactory {

  /** Serialization ID. */
  private static final long serialVersionUID = -3823045554968801054L;

  @Override
  public ICACellRenderer create(ParameterBlock params, Context context) {
    return new ColorCellRenderer();
  }

}
