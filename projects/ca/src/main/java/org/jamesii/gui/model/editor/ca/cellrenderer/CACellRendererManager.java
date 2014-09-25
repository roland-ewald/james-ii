/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.gui.model.editor.ca.cellrenderer.plugintype.CACellRendererFactory;

/**
 * Helper class for managing {@link ICACellRenderer}s
 * 
 * @author Stefan Rybacki
 */
public final class CACellRendererManager {
  /** Cache of registered {@link ICACellRenderer}s */
  private static final List<ICACellRenderer> list =
      new ArrayList<>();
  static {
    Registry registry = SimSystem.getRegistry();

    List<CACellRendererFactory> factories =
        registry.getFactories(CACellRendererFactory.class);

    for (CACellRendererFactory f : factories) {
      ICACellRenderer renderer = f.create(null, SimSystem.getRegistry().createContext());
      if (renderer != null) {
        list.add(renderer);
      }
    }
  }

  /**
   * Hidden constructor.
   */
  private CACellRendererManager() {
  }

  /**
   * Gets the {@link ICACellRenderer}s registered in the system.
   * 
   * @return A list of all registered {@link ICACellRenderer}s
   */
  static List<ICACellRenderer> getAvailableRenderers() {
    return Collections.unmodifiableList(list);
  }
}
