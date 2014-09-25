/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.ICAValueMapper;

/**
 * Abstract base factory class for value mapper factories.
 * 
 * @author Johannes Rössel
 */
public abstract class CAValueMapperFactory extends Factory<ICAValueMapper> {

  /** Serial Version ID. */
  private static final long serialVersionUID = -1768057796708828858L;

  /**
   * Returns whether the value mapper instantiated by this factory supports the
   * given type.
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
   *          A type for which the value mapper should be created.
   * 
   * @return The created value mapper.
   */
  public abstract ICAValueMapper createValueMapper(Class<?> c);
}
