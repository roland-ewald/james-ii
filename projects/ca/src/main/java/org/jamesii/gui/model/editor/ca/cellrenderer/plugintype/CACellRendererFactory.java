/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.model.editor.ca.cellrenderer.ICACellRenderer;

/**
 * Basic factory for all factories that create cell renderers specific for CA
 * models.
 * 
 * @author Stefan Rybacki
 */
public abstract class CACellRendererFactory extends Factory<ICACellRenderer> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 516790989428490242L;

  /**
   * Creates a new instance of a cell renderer, using the given parameters.
 * @param params
   *          Parameters.
 * @return The created cell renderer.
   */
  public abstract ICACellRenderer create(ParameterBlock params, Context context);

}
