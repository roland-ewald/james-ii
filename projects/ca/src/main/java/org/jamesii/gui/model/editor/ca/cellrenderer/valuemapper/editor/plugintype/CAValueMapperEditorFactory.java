/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.ICAValueMapperEditor;

/**
 * Abstract base factory class for value mapper editors.
 * 
 * @author Johannes Rössel
 */
public abstract class CAValueMapperEditorFactory extends
    Factory<ICAValueMapperEditor> {

  /** Serial Version ID. */
  private static final long serialVersionUID = -1768051236708828858L;

  /**
   * Returns whether the value mapper editor instantiated by this factory
   * supports the given type.
   * 
   * @param c
   *          A type.
   * @return {@code true} if the given type is supported by the class
   *         instantiated by this factory or {@code false} otherwise.
   */
  public abstract boolean supports(Class<?> c);

  /**
   * Creates a value mapper.
   * 
   * @param c
   *          The type for which the value mapper should be created.
   * 
   * @return The created value mapper.
   */
  public abstract ICAValueMapperEditor createValueMapperEditor(Class<?> c);

  @Override
  public ICAValueMapperEditor create(ParameterBlock parameters, Context context) {
    return createValueMapperEditor((Class<?>) parameters
        .getSubBlockValue("CLASS"));
  }
}
