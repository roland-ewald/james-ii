/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.plugintype.CAValueMapperEditorFactory;

/**
 * Helper class for managing ValueMapperEditor plugins. Mainly intended for
 * finding a specific editor for a given type.
 * 
 * @author Stefan Rybacki
 * @author Johannes Rössel
 */
public class CAValueMapperEditorManager {

  /**
   * Hidden constructor.
   */
  private CAValueMapperEditorManager() {
  }

  /**
   * Retrieves a value mapper editor for a given type.
   * 
   * @param c
   *          The type for which an editor is needed.
   * @return An instance of an editor fitting the given type or {@code null} if
   *         none is found.
   */
  public static ICAValueMapperEditor getValueMapperEditorFor(Class<?> c) {
    Registry registry = SimSystem.getRegistry();
    List<CAValueMapperEditorFactory> factories =
        registry.getFactories(CAValueMapperEditorFactory.class);
    for (CAValueMapperEditorFactory f : factories) {
      if (f.supports(c)) {
        return f.createValueMapperEditor(c);
      }
    }
    return null;
  }
}
