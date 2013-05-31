/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.preferences;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jamesii.gui.utils.ListenerSupport;

/**
 * Abstract implementation of {@link IPreferencesPage} to provide basic
 * functionality for future implementations. This class should be the base of
 * all future implementations of {@link IPreferencesPage}.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractPreferencesPage implements IPreferencesPage {
  /**
   * support for {@link IPreferencesPageListener}s
   */
  private final ListenerSupport<IPreferencesPageListener> listeners =
      new ListenerSupport<>();

  /**
   * the preferences page's content (cached)
   */
  private JComponent content;

  @Override
  public final synchronized void addPreferencePageListener(
      IPreferencesPageListener l) {
    listeners.addListener(l);
  }

  @Override
  public void cancelPreferences() {
    // should be overridden as needed
  }

  @Override
  public final JComponent getPageContent() {
    if (content == null) {
      content = getPreferencesPageContent();
    }
    if (content == null) {
      content = new JPanel();
    }
    return content;
  }

  /**
   * Delegated method from {@link AbstractPreferencesPage#getPageContent()}
   * 
   * @return the preferences pages content
   */
  protected abstract JComponent getPreferencesPageContent();

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public final synchronized void removePreferencePageListener(
      IPreferencesPageListener l) {
    listeners.removeListener(l);
  }

  /**
   * Notifies registered listeners that the valid state of this preferences page
   * has changed.
   */
  protected final synchronized void fireValidStateChange() {
    for (IPreferencesPageListener l : listeners.getListeners()) {
      if (l != null) {
        l.validStateChanged(this);
      }
    }
  }

  @Override
  public void restoreDefaults() {
    // should be overridden as needed
  }

  @Override
  public void closed() {
    // should be overridden as needed
  }
}
