/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.laf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.perspective.laf.plugintype.LookAndFeelFactory;

/**
 * Manager class that manages look and feels this includes installed look and
 * feels but also provides the ability to register custom look and feels. It can
 * also notify listeners on look and feel changes.
 * 
 * @author Stefan Rybacki
 */
public final class LafManager implements PropertyChangeListener {
  /**
   * singleton instance since this is a static class
   */
  private static final LafManager instance = new LafManager();

  /**
   * list of installed plus registered look and feels
   */
  private final List<LookAndFeelInfo> lafs = new ArrayList<>();

  /**
   * registered listeners
   */
  private final ListenerSupport<ILafChangeListener> listeners =
      new ListenerSupport<>();

  /**
   * flag whether the JAMES II registry was already checked for look and feel
   * plugins
   */
  private boolean registryChecked = false;

  /**
   * hidden constructor
   */
  private LafManager() {
    LookAndFeelInfo[] lFs = UIManager.getInstalledLookAndFeels();

    Collections.addAll(lafs, lFs);

    UIManager.addPropertyChangeListener(this);
  }

  /**
   * Helper method that checks the JAMES II registry for look and feel plugins
   * and loads them if available
   */
  private synchronized void checkRegistry() {
    // also load look and feels available through registry if registry is loaded
    Registry registry = SimSystem.getRegistry();
    if (registry != null && !registryChecked) {
      try {
        List<LookAndFeelFactory> factories =
            registry.getFactories(LookAndFeelFactory.class);
        if (factories == null) {
          factories = new ArrayList<>();
        }

        for (LookAndFeelFactory f : factories) {
          registerLookAndFeel(f.create(null, SimSystem.getRegistry().createContext()));
        }

        registryChecked = true;
      } catch (Throwable t) {
        SimSystem.report(t);
      }
    }
  }

  /**
   * Returns the installed plus registered look and feels
   * 
   * @return the available look and feels
   */
  public static List<LookAndFeelInfo> getLookAndFeels() {
    instance.checkRegistry();
    return new ArrayList<>(instance.lafs);
  }

  /**
   * Returns the currently active look and feel
   * 
   * @return the current look and feel
   */
  public static LookAndFeelInfo getActiveLookAndFeel() {
    return instance.getInfoForLookAndFeel(UIManager.getLookAndFeel());
  }

  /**
   * Sets the look and feel that should be activated
   * 
   * @param info
   *          the info for the look and feel to activate
   */
  public static void setActiveLookAndFeel(LookAndFeelInfo info) {
    if (info == null) {
      return;
    }
    instance.checkRegistry();
    WindowManagerManager.getWindowManager().setLookAndFeel(info.getClassName());
    instance.fireActiveLookAndFeelChanged();
  }

  /**
   * Helper method that notifies listeners that a look and feel was registered
   * 
   * @param info
   *          the look and feel info for the look and feel that was registered
   */
  private synchronized void fireLookAndFeelAdded(LookAndFeelInfo info) {
    for (ILafChangeListener l : listeners) {
      l.lookAndFeelAdded(info);
    }
  }

  /**
   * Helper method that notifies the listeners that a different look and feel
   * was activated
   */
  private synchronized void fireActiveLookAndFeelChanged() {
    for (ILafChangeListener l : listeners) {
      l.activeLookAndFeelChanged(getActiveLookAndFeel());
    }
  }

  /**
   * Helper method that returns for a given look and feel that look and feel
   * info (if registered)
   * 
   * @param laf
   *          the look and feel the info is to be returned
   * @return the look and feel info if available
   */
  private LookAndFeelInfo getInfoForLookAndFeel(LookAndFeel laf) {
    if (laf == null) {
      return null;
    }
    for (LookAndFeelInfo i : lafs) {
      if (i.getClassName().equals(laf.getClass().getName())) {
        return i;
      }
    }
    return null;
  }

  /**
   * Registers a custom look and feel providing a look and feel info object for
   * that look and feel
   * 
   * @param info
   *          the look and feel info for the look and feel to register
   */
  public static void registerLookAndFeel(LookAndFeelInfo info) {
    if (info == null) {
      return;
    }
    if (!instance.lafs.contains(info)) {
      instance.lafs.add(info);
      instance.fireLookAndFeelAdded(info);
    }
  }

  /**
   * Adds a look and feel change listener
   * 
   * @param l
   *          the listener to add
   */
  public static synchronized void addLafChangeListener(ILafChangeListener l) {
    instance.listeners.addListener(l);
  }

  /**
   * Removes a previously registered look and feel change listener
   * 
   * @param l
   *          the listener to remove
   */
  public static synchronized void removeLafChangeListener(ILafChangeListener l) {
    instance.listeners.removeListener(l);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if ("lookAndFeel".equals(evt.getPropertyName())) {
      fireActiveLookAndFeelChanged();
    }
  }
}
