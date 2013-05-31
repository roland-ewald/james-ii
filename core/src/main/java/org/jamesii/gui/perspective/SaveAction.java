/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import javax.swing.Icon;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowListener;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.action.AbstractAction;

/**
 * The Class SaveAction.
 * 
 * @author Stefan Rybacki
 */
class SaveAction extends AbstractAction implements IWindowListener {

  /** The window manager. */
  private final IWindowManager windowManager;

  /** The last active window. */
  private IWindow lastActiveWindow;

  /**
   * Instantiates a new save action.
   * 
   * @param id
   *          the id
   * @param label
   *          the label
   * @param windowManager
   *          the window manager
   * @param icon
   *          the icon
   * @param paths
   *          the paths
   * @param keyStroke
   *          the key stroke
   * @param mnemonic
   *          the mnemonic
   * @param w
   *          the window the action is used in
   */
  public SaveAction(String id, String label, IWindowManager windowManager,
      Icon icon, String[] paths, String keyStroke, Integer mnemonic, IWindow w) {
    super(id, label, icon, paths, keyStroke, mnemonic, w);
    if (windowManager == null) {
      throw new IllegalArgumentException("windowManager can't be null!");
    }

    this.windowManager = windowManager;
    windowManager.addWindowListener(this);

    IWindow window = windowManager.getActiveWindow();
    windowActivated(window);
  }

  @Override
  public synchronized void execute() {
    if (lastActiveWindow != null && lastActiveWindow.isSaveable()) {
      try {
        lastActiveWindow.save();
      } catch (RuntimeException e) {
        SimSystem.report(e);
      }
    }
  }

  @Override
  public final synchronized void windowActivated(IWindow window) {
    setEnabled(window != null && window.isSaveable());
    if (window != null) {
      lastActiveWindow = window;
    }
  }

  @Override
  public void windowClosed(IWindow window) {
  }

  @Override
  public void windowDeactivated(IWindow window) {
    IWindow w = windowManager.getActiveWindow();
    setEnabled(w != null && w.isSaveable());
  }

  @Override
  public void windowOpened(IWindow window) {
  }

}
