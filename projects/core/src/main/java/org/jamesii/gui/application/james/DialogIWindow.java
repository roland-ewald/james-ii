/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.WindowConstants;

import org.jamesii.gui.application.ApplicationDialog;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowChangeListener;
import org.jamesii.gui.application.IWindowListener;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.IAction;

/**
 * Class internally used to contain a given {@link IWindow} in a separate
 * dialog. This is used for the {@link org.jamesii.gui.application.Contribution}
 * {@link org.jamesii.gui.application.Contribution#DIALOG}
 * 
 * @author Stefan Rybacki
 */
final class DialogIWindow extends ApplicationDialog implements IWindowListener,
    IWindowChangeListener {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -1619859815323971844L;

  /**
   * Creates a new dialog for the given {@link IWindow} using the given
   * {@link org.jamesii.gui.application.IWindowManager} and the relative action url.
   * 
   * @param window
   *          the {@link IWindow} to contain
   * @param windowManager
   *          the window manager for the {@link IWindow}
   * @return the dialog created from the given {@link IWindow}
   */
  public static DialogIWindow create(final IWindow window,
      IWindowCreator creator) {
    if (window == null) {
      throw new IllegalArgumentException("window can't be null!");
    }

    final DialogIWindow dialogWindow = new DialogIWindow(creator);

    dialogWindow.setWindow(window);

    return dialogWindow;
  }

  /**
   * Hidden constructor due to only allow static access to functionality of this
   * class
   * 
   * @param windowManager
   *          the used window manager for the given {@link IWindow}
   */
  private DialogIWindow(IWindowCreator windowCreator) {
    this.windowCreator = windowCreator;
    WindowManagerManager.getWindowManager().addWindowListener(this);

    setLayout(new BorderLayout());
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        WindowManagerManager.getWindowManager().closeWindow(window);
      }
    });
  }

  /**
   * pane to put the window in
   */
  private WindowPane pane;

  /**
   * the window
   */
  private IWindow window;

  private final IWindowCreator windowCreator;

  /**
   * Sets the given {@link IWindow} as current window to contain
   * 
   * @param window
   *          the new window
   */
  private void setWindow(IWindow window) {
    if (window == null) {
      throw new IllegalArgumentException("window can't be null!");
    }

    setTitle(window.getTitle());
    setIcon(window.getWindowIcon());
    this.window = window;

    window.addWindowChangeListener(this);

    pane = new WindowPane(windowCreator);
    pane.addWindow(window);

    add(pane, BorderLayout.CENTER);

    // TODO sr137: if the size of the dialog was changed last time try to
    // reload that custom size and set it explicitly

    setModal(false);

    setVisible(true);
    setLocationRelativeTo(WindowManagerManager.getWindowManager()
        .getMainWindow());
  }

  /**
   * Closes the dialog with also closing the contained {@link IWindow} if the
   * specified window is the currently managed window.
   * 
   * @param w
   *          the window to be closed
   * @return true if the window was closed by this dialog
   */
  public boolean closeWindow(IWindow w) {
    if (w == window) {
      pane.removeWindow(window);
      if (window != null) {
        window.removeWindowChangeListener(this);
      }
      setVisible(false);
      dispose();
      return true;
    }
    return false;
  }

  @Override
  public void windowActivated(IWindow w) {
    // nothing to do
  }

  @Override
  public void windowClosed(IWindow w) {
    if (window != null && w == window) {
      closeWindow(window);
    }
  }

  @Override
  public void windowDeactivated(IWindow w) {
    // nothing to do
  }

  @Override
  public void windowOpened(IWindow w) {
    // nothing to do
  }

  @Override
  public void windowActionsChanged(IWindow w, IAction[] oldActions) {
    // nothing to do
  }

  @Override
  public void windowIconChanged(IWindow w) {
    if (w == window && window != null) {
      setIcon(window.getWindowIcon());
    }
  }

  @Override
  public void windowTitleChanged(IWindow w) {
    if (w == window && window != null) {
      setTitle(window.getTitle());
    }
  }

  /**
   * If the specified window is the one managed by this dialog focus is
   * requested for this dialog.
   * 
   * @param w
   *          the window to select
   */
  public void setSelectedWindow(IWindow w) {
    if (w == window) {
      requestFocusInWindow();
    }
  }

  /**
   * @return the {@link IWindow} the dialog is holding
   */
  public IWindow getWindow() {
    return window;
  }

  @Override
  public void setVisible(boolean b) {
    if (!b) {
      WindowManagerManager.getWindowManager().removeWindowListener(this);
    }
    super.setVisible(b);
  }
}
