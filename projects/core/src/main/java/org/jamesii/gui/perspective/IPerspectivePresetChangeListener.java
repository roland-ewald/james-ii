/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

/**
 * Interface for listeners that can be notified whenever a preset of
 * {@link IPerspective}s namely a {@link PerspectivePreset} was deleted or added
 * from or to the list of presets.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IPerspectivePresetChangeListener {
  /**
   * Called whenever a preset is removed
   * 
   * @param p
   *          the removed preset
   */
  void presetDeleted(PerspectivePreset p);

  /**
   * Called whenever a preset is added
   * 
   * @param p
   *          the added preset
   */
  void presetAdded(PerspectivePreset p);
}
