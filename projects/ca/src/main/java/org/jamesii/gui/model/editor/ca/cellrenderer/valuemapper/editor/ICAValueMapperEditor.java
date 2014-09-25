/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor;

import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.ICAValueMapper;

/**
 * Interface for editors that change mappings in {@link ICAValueMapper}s.
 * 
 * @author Johannes Rössel
 */
public interface ICAValueMapperEditor {

  /**
   * Causes the ValueMapperEditor to prompt for changing a specific mapping for
   * an input. The input name, value and corresponding ValueMapper are provided
   * for the editor to reference. This method is supposed to set the edited
   * mapping in the ValueMapper but returns the new mapping for convenience.
   * 
   * @param inputName
   *          The name of the input of the ValueMapper.
   * @param value
   *          The value of the cell, used for lookup in the mapper.
   * @param usedMapper
   *          The value mapper for which the mapping is changed.
   * @return The new mapping for the specified value.
   */
  Object editMappingFor(String inputName, Object value,
      ICAValueMapper usedMapper);

  /**
   * Returns the type that is supposed to be supported by this editor.
   * 
   * @return A type that is supported by this editor.
   */
  Class<?> getSupportedType();
}
