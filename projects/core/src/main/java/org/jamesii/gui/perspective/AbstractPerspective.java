/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.ApplicationManagerManager;
import org.jamesii.gui.application.IApplicationManager;
import org.jamesii.gui.application.IPerspectiveChangeListener;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.preferences.IPreferencesPage;

/**
 * Abstract class that implements basic functionality for a class that
 * implements an {@link IPerspective} interface.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractPerspective implements IPerspective {
  /**
   * Cached actions so that they only need to be generated once
   */
  private List<IAction> actions = null;

  /**
   * list of listeners
   */
  private final ListenerSupport<IPerspectiveChangeListener> listeners =
      new ListenerSupport<>();

  @Override
  public final List<IAction> getActions() {
    // lazy initialization of actions
    if (actions != null) {
      return actions;
    }

    setActions(generateActions());

    return actions;
  }

  /**
   * Helper method that creates an empty list if the given list is {@code null}
   * or creates a list from the given list (using defensive copy)
   * 
   * @param a
   *          the list of {@link IAction}s to set
   */
  private void setActions(List<IAction> a) {
    if (a == null) {
      this.actions = new ArrayList<>();
    } else {
      this.actions = new ArrayList<>(a);
    }
  }

  /**
   * Use this method to notify listeners and to provide new actions. Note: new
   * actions must contain all actions the perspective provides at the current
   * state and not only the changed actions
   * 
   * @param newActions
   *          a list of actions the perspective provides
   */
  protected final synchronized void fireActionsChanged(List<IAction> newActions) {
    IAction[] old = getActions().toArray(new IAction[getActions().size()]);
    setActions(newActions);

    for (IPerspectiveChangeListener p : listeners.getListeners()) {
      if (p != null) {
        p.perspectiveActionsChanged(this, old);
      }
    }
  }

  /**
   * Implement this method to provide the actions that are needed to implement
   * the {@link IPerspective} interface. This method is called once on
   * initializing the perspective.
   * 
   * @return list of the perspective's actions
   */
  protected abstract List<IAction> generateActions();

  /**
   * @return the current application manager
   */
  protected final IApplicationManager getApplicationManager() {
    return ApplicationManagerManager.getApplicationManager();
  }

  /**
   * @return the current window manager
   */
  protected final IWindowManager getWindowManager() {
    return WindowManagerManager.getWindowManager();
  }

  @Override
  public boolean isMandatory() {
    return false;
  }

  @Override
  public void perspectiveClosed() {
    // should be overridden as needed
  }

  @Override
  public void openPerspective() {
    // should be overridden as needed
  }

  @Override
  public List<IPreferencesPage> getPreferencesPages() {
    return null;
  }

  @Override
  public synchronized void addPerspectiveChangeListener(
      IPerspectiveChangeListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public synchronized void removePerspectiveChangeListener(
      IPerspectiveChangeListener listener) {
    listeners.removeListener(listener);
  }
}
