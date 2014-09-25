/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper;

/**
 * Interface for value mappers for CA models. These are used for mapping CA
 * states (values) to other types, like colours, images or strings, mostly
 * intended for display of states.
 * 
 * @author Johannes Rössel
 */
public interface ICAValueMapper {
  /**
   * Returns the type supported by this value mapper.
   * 
   * @return A type, supported by this mapper.
   */
  Class<?> getSupportedType();

  /**
   * Retrieves the mapping for a specific value. This may return {@code null} if
   * no mapping is present.
   * 
   * @param value
   *          The state (value) for which a mapping should be retrieved.
   * @return The mapping for the given value.
   */
  Object getMappingFor(Object value);

  /**
   * Sets the mapping for a specific value.
   * 
   * @param value
   *          The value for which a mapping should be set.
   * @param mapping
   *          The new mapping for the given value.
   */
  void setMappingFor(Object value, Object mapping);

  /**
   * Adds a listener that gets notified whenever a mapping in this value mapper
   * changes.
   * 
   * @param l
   *          The listener to add.
   */
  void addMappingChangedListener(IMappingChangedListener l);

  /**
   * Removes a previously added {@link IMappingChangedListener}.
   * 
   * @param l
   *          The listener to remove.
   */
  void removeMappingChangedListener(IMappingChangedListener l);
}
