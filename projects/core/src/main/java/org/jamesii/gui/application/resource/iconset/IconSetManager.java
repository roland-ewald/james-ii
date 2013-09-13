/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource.iconset;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that holds the current set icon set. Classes that need
 * standard icons defined through an {@link IIconSet} should use this class to
 * retrieve the currently set icon set.
 * 
 * @see org.jamesii.gui.application.resource.BasicResources
 * @author Stefan Rybacki
 */
public final class IconSetManager {
  /**
   * private singleton instance of manager
   */
  private static final IconSetManager INSTANCE = new IconSetManager();

  /**
   * icon set initialized with a default icon set
   */
  private IIconSet iconSet = DefaultIconSet.getInstance();

  /**
   * list of registered icon sets
   */
  private List<IIconSet> iconSets = new ArrayList<>();

  private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

  /**
   * omitted constructor
   */
  private IconSetManager() {
    iconSets.add(DefaultIconSet.getInstance());
  }

  /**
   * Changes the icon set that is returned by this class
   * 
   * @param set
   *          the icon set to set
   */
  public static void setDefaultIconSet(IIconSet set) {
    IIconSet old = INSTANCE.iconSet;
    INSTANCE.iconSet = set;
    INSTANCE.listeners.firePropertyChange("iconSet", old, set);
  }

  /**
   * @return the currently set icon set
   */
  public static IIconSet getIconSet() {
    return INSTANCE.iconSet;
  }

  /**
   * Registers an icon set so that is added to the list of available icon sets
   * 
   * @see #getAvailableIconSets()
   * @param set
   *          the icon set to register
   */
  public static void registerIconSet(IIconSet set) {
    if (set != null && !(INSTANCE.iconSets.contains(set))) {
      INSTANCE.iconSets.add(set);
    }
  }

  /**
   * Adds the property change listener.
   * 
   * @param l
   *          the listener to add
   */
  public static synchronized void addPropertyChangeListener(
      PropertyChangeListener l) {
    INSTANCE.listeners.addPropertyChangeListener(l);
  }

  /**
   * Removes the property change listener.
   * 
   * @param l
   *          the listener to remove
   */
  public static synchronized void removePropertyChangeListener(
      PropertyChangeListener l) {
    INSTANCE.listeners.removePropertyChangeListener(l);
  }

  /**
   * @return all registered icon sets
   */
  public static List<IIconSet> getAvailableIconSets() {
    return new ArrayList<>(INSTANCE.iconSets);
  }
}
