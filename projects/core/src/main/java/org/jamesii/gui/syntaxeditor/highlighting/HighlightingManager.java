/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.syntaxeditor.highlighting.plugintype.HighlightingFactory;

/**
 * Manager class providing access to all through James plugins available
 * highlighters for a given input object.
 * 
 * @author Stefan Rybacki
 */
public final class HighlightingManager {
  /**
   * omitted constructor
   */
  private HighlightingManager() {
  }

  /**
   * @param objectToSupport
   *          the input object highlighters are requested for
   * @return a list of available highlighters for given input object
   */
  public static List<IHighlighter> getAvailableHighlightersFor(
      Object objectToSupport) {
    List<IHighlighter> highlighters = new ArrayList<>();
    Registry registry = SimSystem.getRegistry();

    if (registry != null) {
      List<HighlightingFactory> highlightingFactories =
          SimSystem.getRegistry().getFactories(HighlightingFactory.class);

      if (highlightingFactories == null) {
        highlightingFactories = new ArrayList<>();
      }

      ParameterBlock params = new ParameterBlock();

      for (HighlightingFactory f : highlightingFactories) {
        try {
          if (f.supportsInput(objectToSupport)) {
            IHighlighter highlighter = f.create(params);

            highlighters.add(highlighter);
          }
        } catch (Throwable e) {
          SimSystem.report(Level.WARNING,
              "Couldn't create Highlighter " + f.getName(), e);
        }
      }
    }

    return highlighters;
  }
}
