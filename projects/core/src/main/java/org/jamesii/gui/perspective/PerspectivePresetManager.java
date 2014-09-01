/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.gui.application.preferences.Preferences;
import org.jamesii.gui.utils.ListenerSupport;

/**
 * Static class for managing and using installed {@link PerspectivePreset}s.
 * 
 * @author Stefan Rybacki
 */
public final class PerspectivePresetManager {
  /**
   * list of available presets
   */
  private final List<PerspectivePreset> presets = new ArrayList<>();

  /**
   * list of listeners
   */
  private final ListenerSupport<IPerspectivePresetChangeListener> listeners =
      new ListenerSupport<>();

  /**
   * singleton instance of preset manager
   */
  private static final PerspectivePresetManager INSTANCE =
      new PerspectivePresetManager();

  /**
   * Constructor ommitted
   */
  private PerspectivePresetManager() {
    // load presets from Preferences
    ArrayList<PerspectivePreset> p =
        Preferences.get("org.jamesii.perspectives.presets");
    if (p != null) {
      presets.addAll(p);
    }
  }

  /**
   * Helper function used to notify attached listeners when a preset is added.
   * 
   * @param p
   *          the preset that was added
   */
  private synchronized void firePresetAdded(PerspectivePreset p) {
    for (IPerspectivePresetChangeListener pp : listeners.getListeners()) {
      if (pp != null) {
        pp.presetAdded(p);
      }
    }
  }

  /**
   * Helper function used to notify attached listeners when a preset is deleted.
   * 
   * @param p
   *          the preset that was removed
   */
  private synchronized void firePresetDeleted(PerspectivePreset p) {
    for (IPerspectivePresetChangeListener pp : listeners.getListeners()) {
      if (pp != null) {
        pp.presetDeleted(p);
      }
    }
  }

  /**
   * Adds a new preset
   * 
   * @param p
   *          the preset to add
   */
  public static synchronized void addPreset(PerspectivePreset p) {
    if (p != null && !INSTANCE.presets.contains(p)) {
      INSTANCE.presets.add(p);
      INSTANCE.firePresetAdded(p);
      // store new list to preferences
      Preferences.put("org.jamesii.perspectives.presets", new ArrayList<>(
          INSTANCE.presets));
    }
  }

  /**
   * Deletes a preset
   * 
   * @param p
   *          the preset to delete
   */
  public static synchronized void deletePreset(PerspectivePreset p) {
    if (p != null && INSTANCE.presets.contains(p)) {
      INSTANCE.presets.remove(p);
      INSTANCE.firePresetDeleted(p);
      // store new list to preferences
      Preferences.put("org.jamesii.perspectives.presets", new ArrayList<>(
          INSTANCE.presets));
    }
  }

  /**
   * Is used to provide a preset that activates the given list of perspectives.
   * This can be used to find duplicates and or to provide a way of preselecting
   * a preset according to a selected set of perspectives.
   * 
   * @param perspectives
   *          a list of perspectives
   * @return null if no proper preset was found a preset else
   */
  public static PerspectivePreset findPresetForPerspectives(
      List<IPerspective> perspectives) {
    if (perspectives == null) {
      return null;
    }
    // try to find a preset with the same list of perspectives
    for (PerspectivePreset p : INSTANCE.presets) {
      // check list of perspectives with list of perspectives given
      String[] presetPerspectives = p.getPerspectives();
      // first check count of perspectives in both lists
      if (presetPerspectives.length == perspectives.size()) {
        boolean equal = true;
        // now check whether each list of list A is in list B
        for (IPerspective persp : perspectives) {
          if (persp == null) {
            return null;
          }

          // try to find persp in list of perspectives of preset
          boolean found = false;
          for (String presetPerspective : presetPerspectives) {
            if (persp.getClass().getName().equals(presetPerspective)) {
              found = true;
              break;
            }
          }

          equal = equal && found;
          if (!equal) {
            break;
          }
        }

        if (equal) {
          return p;
        }
      }
    }

    return null;
  }

  /**
   * Adds a listener that is notified on changes to the list of presets
   * 
   * @param listener
   *          the listener to attach
   */
  public static synchronized void addPresetChangeListener(
      IPerspectivePresetChangeListener listener) {
    INSTANCE.listeners.addListener(listener);
  }

  /**
   * Removes a previously attached listener
   * 
   * @param listener
   *          the listener to remove
   */
  public static synchronized void removePresetChangeListener(
      IPerspectivePresetChangeListener listener) {
    INSTANCE.listeners.removeListener(listener);
  }

  /**
   * @return all available presets
   */
  public static List<PerspectivePreset> getAvailablePresets() {
    return new ArrayList<>(INSTANCE.presets);
  }

  /**
   * Convenience method to add a bunch of preset at once. Note: Presets already
   * in the list are skipped
   * 
   * @param ps
   *          a list of presets to add
   */
  public static void setPresets(List<PerspectivePreset> ps) {
    if (ps == null) {
      return;
    }

    // first remove all presets that are not in ps
    for (int i = INSTANCE.presets.size() - 1; i >= 0; i--) {
      PerspectivePreset p = INSTANCE.presets.get(i);

      if (!ps.contains(p)) {
        deletePreset(p);
      }
    }

    // now add all other presets
    for (PerspectivePreset p : ps) {
      addPreset(p);
    }
  }

  /**
   * Sets the current active preset
   * 
   * @param p
   *          the preset to set
   */
  public static void setPreset(PerspectivePreset p) {
    for (IPerspective persp : PerspectivesManager.getAvailablePerspectives()) {
      if ((p.isInPreset(persp) || persp.isMandatory())
          && !PerspectivesManager.isOpen(persp)) {
        PerspectivesManager.openPerspective(persp);
        continue;
      }

      if (!p.isInPreset(persp) && PerspectivesManager.isOpen(persp)) {
        PerspectivesManager.closePerspective(persp);
      }
    }
  }

}
