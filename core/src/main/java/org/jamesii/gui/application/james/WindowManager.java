/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JComponent;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.AbstractWindowManager;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowChangeListener;
import org.jamesii.gui.application.action.ActionManager;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Window manager. That uses the {@link IWindowCreator} interface to delegate
 * the actual window creation process to a separate handler.
 * 
 * @author Stefan Rybacki
 * 
 */
public class WindowManager extends AbstractWindowManager implements
    IWindowChangeListener {
  /**
   * map of containers associated with the given window to determine correct
   * window to given focus
   */
  private final Map<IWindow, Component> containers = new HashMap<>();

  /**
   * the actual window creator responsible for window creation, closing on the
   * screen
   */
  private IWindowCreator creator;

  /**
   * counter to generate unique relative action urls
   */
  private final AtomicInteger number = new AtomicInteger();

  /**
   * stores relative urls for registered windows
   */
  private final Map<IWindow, String> relativeURLs = new WeakHashMap<>();

  /**
   * Creates a new instance of this window manager using the given window
   * creator.
   * 
   * @param creator
   *          the creator to use for actual window creation
   * @param windowSize
   *          the initial window size
   * @param position
   *          the window position
   */
  public WindowManager(IWindowCreator creator, Dimension windowSize,
      Point position) {
    super(windowSize, position);
    if (creator == null) {
      throw new IllegalArgumentException("creator can't be null!");
    }
    this.creator = creator;
  }

  @Override
  protected Component getContainerOfWindow(IWindow w) {
    return containers.get(w);
  }

  /**
   * Helper method that generates unique relative urls for use in
   * {@link #getRelativeURLFor(IWindow)}
   * 
   * @return a new unique relative url
   */
  private String nextRelativeURL() {
    return String.format("window.toolbar.%d", number.incrementAndGet());
  }

  @Override
  protected void windowAdded(IWindow window) {
    if (window != null) {
      // store container for window and show window
      String relativeURL = nextRelativeURL();
      relativeURLs.put(window, relativeURL);

      // get actions and register them in action manager
      IAction[] actions = null;
      try {
        actions = window.getActions();
      } catch (Throwable e) {
        SimSystem.report(e);
      }
      if (actions != null) {
        for (IAction a : actions) {
          ActionManager.registerActionRelative(a, relativeURL);
        }
      }

      window.addWindowChangeListener(this);

      containers.put(window, creator.showWindow(window));
      activateWindow(window);
    }
  }

  @Override
  protected void windowClosed(IWindow window) {
    creator.closeWindow(window);
    containers.remove(window);
    window.removeWindowChangeListener(this);
  }

  @Override
  protected void windowActivated(final IWindow window) {
    try {
      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          creator.makeVisible(window);
        }
      });
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
    }
  }

  @Override
  public String getRelativeURLFor(IWindow window) {
    String url = relativeURLs.get(window);
    if (url == null) {
      url = nextRelativeURL();
      relativeURLs.put(window, url);
    }
    return url;
  }

  @Override
  public void exitingApplication() {
    super.exitingApplication();
    creator.exitingApplication();
  }

  @Override
  public JComponent createContainer(IWindow window) {
    return creator.createContainer(window, getRelativeURLFor(window));
  }

  @Override
  public void windowActionsChanged(IWindow window, IAction[] oldActions) {
    if (getWindows().contains(window)) {
      String rootURL = getRelativeURLFor(window);
      if (rootURL != null) {
        ActionManager.removeRelative(oldActions, rootURL);

        // get actions and register them in action manager
        IAction[] actions = null;
        try {
          actions = window.getActions();
        } catch (Throwable e) {
          SimSystem.report(e);
        }
        if (actions != null) {
          for (IAction a : actions) {
            ActionManager.registerActionRelative(a, rootURL);
          }
        }
      }
    }
  }

  @Override
  public void windowIconChanged(IWindow window) {
  }

  @Override
  public void windowTitleChanged(IWindow window) {
  }

  @Override
  public void setLookAndFeel(String lafclassname) {
    super.setLookAndFeel(lafclassname);
    ActionManager.updateUI();
  }

  @Override
  protected void removeRelativeURLFor(IWindow window) {
    relativeURLs.remove(window);
  }

}
