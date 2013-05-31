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
 * The Class SaveAsAction.
 * 
 * @author Stefan Rybacki
 */
class SaveAsAction extends AbstractAction implements IWindowListener {

  /** The window manager. */
  private final IWindowManager windowManager;

  /**
   * Instantiates a new save as action.
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
  public SaveAsAction(String id, String label, IWindowManager windowManager,
      Icon icon, String[] paths, String keyStroke, Integer mnemonic, IWindow w) {
    super(id, label, icon, paths, keyStroke, mnemonic, w);
    if (windowManager == null) {
      throw new IllegalArgumentException("windowManager can't be null!");
    }

    this.windowManager = windowManager;
    windowManager.addWindowListener(this);

    IWindow window = windowManager.getActiveWindow();
    setEnabled(window != null && window.isSaveable());
  }

  @Override
  public void execute() {
    IWindow window = windowManager.getActiveWindow();
    if (window != null && window.isSaveable()) {
      try {
        window.saveAs();
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  @Override
  public void windowActivated(IWindow window) {
    setEnabled(window != null && window.isSaveable());
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
