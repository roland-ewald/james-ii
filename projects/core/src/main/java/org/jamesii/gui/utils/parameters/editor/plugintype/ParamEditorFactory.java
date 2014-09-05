/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.editable.IEditor;

/**
 * Super class of all parameter editor factories.
 * 
 * Date: 06.07.2007
 * 
 * @author Roland Ewald
 * 
 * 
 */
public abstract class ParamEditorFactory extends Factory<IEditor<?>> implements
    IParameterFilterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8628638326899629338L;

  /**
   * Returns editor for given editable and parameters.
 * @param params
   *          factory parameters
 * @return editor for this editable variable
   */
  @Override
  public abstract IEditor<?> create(ParameterBlock params, Context context);

}
