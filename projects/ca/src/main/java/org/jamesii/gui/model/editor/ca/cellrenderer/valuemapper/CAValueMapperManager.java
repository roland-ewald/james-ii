/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.plugintype.CAValueMapperFactory;

/**
 * Helper class for managing ValueMappers. Mainly intended for finding a
 * specific ValueMapper for a given type.
 * <p>
 * TODO: This class doesn't seem to do anything useful since ValueMappers are
 * plugins but have no factories ... at least as of now.
 * 
 * @author Stefan Rybacki
 * @author Johannes Rössel
 */
public class CAValueMapperManager {

  /**
   * Hidden constructor.
   */
  private CAValueMapperManager() {
  }

  /**
   * Finds a value mapper for a given type and returns it.
   * 
   * @param c
   *          The type for which a value mapper is needed.
   * @return An instance of a value mapper that handles the given type or
   *         {@code null} if none is found.
   */
  static ICAValueMapper getValueMapperFor(Class<?> c) {
    Registry registry = SimSystem.getRegistry();
    List<CAValueMapperFactory> factories =
        registry.getFactories(CAValueMapperFactory.class);
    for (CAValueMapperFactory f : factories) {
      if (f.supports(c)) {
        return f.createValueMapper(null);
      }
    }
    return null;
  }
}
