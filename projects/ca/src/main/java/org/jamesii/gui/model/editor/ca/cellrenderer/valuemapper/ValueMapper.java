/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic value mapper, suitable for any object.
 * 
 * @author Stefan Rybacki
 * @author Johannes Rössel
 */
public class ValueMapper extends AbstractValueMapper {

  /**
   * Constructor that creates this value mapper for a specific supported type.
   * 
   * @param supportedType
   *          The type this value mapper is supposed to support.
   */
  public ValueMapper(Class<?> supportedType) {
    super(supportedType);
  }

  /** The mappings. */
  private Map<Object, Object> map = new HashMap<>();

  @Override
  public Object getMappingFor(Object value) {
    return map.get(value);
  }

  @Override
  public void setMappingFor(Object value, Object mapping) {
    map.put(value, mapping);
    fireMappingChanged();
  }

}
