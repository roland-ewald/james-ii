/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.Dimension;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.syntaxeditor.JamesUndoManager;

/**
 * An abstract class that already implements a lot of functionality of
 * {@link IWindow} and should be the basis of any class that implements
 * {@link IWindow}.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractWindow implements IWindow {
  private static final int STANDARDWIDTH = 100;

  private static final int STANDARDHEIGHT = 100;

  /**
   * the window title
   */
  private String title;

  /**
   * the window icon
   */
  private Icon icon;

  /**
   * the window contribution
   */
  private Contribution contribution;

  /**
   * the cached actions for the window
   */
  private IAction[] actions;

  /**
   * the window manager managing this window
   */
  private IWindowManager windowManager;

  /**
   * flag indicating whether actions are already loaded (whether
   * {@link #actions} already caches the actions)
   */
  private boolean actionsLoaded = false;

  /**
   * support for listeners
   */
  private final ListenerSupport<IWindowChangeListener> listeners =
      new ListenerSupport<>();

  /**
   * the actual window content
   */
  private JComponent content;

  /**
   * read/write lock for {@link #firstActivated}
   */
  private Lock openedLock = new ReentrantLock();

  /**
   * flag indicating whether the window is activated for the first time
   */
  private boolean firstActivated = false;

  /**
   * Constructor for {@link AbstractWindow} that initializes an abstract
   * {@link IWindow} implementation with the given parameters.
   * 
   * @param title
   *          the window title
   * @param icon
   *          the window icon
   * @param contribution
   *          the window contribution
   */
  public AbstractWindow(String title, Icon icon, Contribution contribution) {
    setTitle(title);
    setIcon(icon);
    this.contribution = contribution;
  }

  /**
   * Implement this method to provide custom actions for the window
   * 
   * @return actions for the window
   * @see IAction
   */
  protected abstract IAction[] generateActions();

  /**
   * sets the window title
   * 
   * @param t
   *          the new title
   */
  protected final void setTitle(String t) {
    title = t;
    fireTitleChanged();
  }

  /**
   * sets the window icon
   * 
   * @param i
   *          the new icon
   */
  protected final void setIcon(Icon i) {
    icon = i;
    fireIconChanged();
  }

  @Override
  public Contribution getContribution() {
    if (contribution == null) {
      return Contribution.EDITOR; // use as fallback
    }
    return contribution;
  }

  @Override
  public final synchronized IAction[] getActions() {
    if (!actionsLoaded) {
      actions = generateActions();
      actionsLoaded = true;
    }
    if (actions == null) {
      return new IAction[0];
    }
    return actions.clone();
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public Icon getWindowIcon() {
    return icon;
  }

  @Override
  public void installWindowManager(IWindowManager manager) {
    windowManager = manager;
  }

  /**
   * @return the window manager responsible for managing this window
   */
  protected final IWindowManager getWindowManager() {
    return windowManager;
  }

  @Override
  public void addWindowChangeListener(IWindowChangeListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public void removeWindowChangeListener(IWindowChangeListener listener) {
    listeners.removeListener(listener);
  }

  @Override
  public boolean canClose() {
    return true;
  }

  @Override
  public void windowActivated() {
    openedLock.lock();

    try {
      if (!firstActivated) {
        firstActivated = true;
        windowOpened();
      }
    } finally {
      openedLock.unlock();
    }
  }

  /**
   * Provides a way to determine when the window was opened (bascially this
   * method is called when the window is activated for the first time)
   */
  protected void windowOpened() {
    // should be overridden as needed
  }

  @Override
  public void windowClosed() {
    // should be overridden as needed
  }

  @Override
  public void windowDeactivated() {
    // should be overridden as needed
  }

  /**
   * Notifies the registered listeners that the window title has changed
   */
  protected final synchronized void fireTitleChanged() {
    for (IWindowChangeListener l : listeners.getListeners()) {
      if (l != null) {
        l.windowTitleChanged(this);
      }
    }
  }

  /**
   * Notifies the registered listeners that the window icon has changed
   */
  protected final synchronized void fireIconChanged() {
    for (IWindowChangeListener l : listeners.getListeners()) {
      if (l != null) {
        l.windowIconChanged(this);
      }
    }
  }

  /**
   * Notifies the registered listeners that the window actions have changed
   * 
   * @param oldActions
   *          the actions before the change
   */
  protected final synchronized void fireActionsChanged(IAction[] oldActions) {
    actionsLoaded = false;
    for (IWindowChangeListener l : listeners.getListeners()) {
      if (l != null) {
        l.windowActionsChanged(this, oldActions);
      }
    }
  }

  @Override
  public boolean isSaveable() {
    return false;
  }

  @Override
  public void save() {
    // should be overridden as needed
  }

  @Override
  public void saveAs() {
    // should be overridden as needed
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(STANDARDWIDTH, STANDARDHEIGHT);
  }

  @Override
  public String getWindowID() {
    return getClass().getName();
  }

  @Override
  public JamesUndoManager getUndoManager() {
    return null;
  }

  @Override
  public boolean isUndoRedoSupported() {
    return false;
  }

  /**
   * Implement this method to provide the content of the window. The method is
   * called only once and the content will be cached by {@link AbstractWindow}
   * so the implementation does not need to cache the generated content.
   * 
   * @return the window content
   */
  protected abstract JComponent createContent();

  @Override
  public final JComponent getContent() {
    if (content == null) {
      content = createContent();
    }
    return content;
  }

  /**
   * @see IWindowManager#closeWindow(IWindow)
   * @return true if window could be closed
   */
  protected final boolean close() {
    return getWindowManager().closeWindow(this);
  }

}
