/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.syntaxeditor.JamesUndoManager;

/**
 * Main interface for windows/view that are used in the JAMES II GUI. Implement
 * this interface to provide custom plugable, drag and dropable and
 * automatically manages views into the JAMES II GUI. A good starting point for
 * custom views is {@link AbstractWindow}.
 * 
 * @author Stefan Rybacki
 * @see AbstractWindow
 * @see IWindowManager
 */
public interface IWindow {
  /**
   * Provides the {@link IAction}s for the window that then are displayed in the
   * windows toolbar. This way it is possible to provide a window specific
   * toolbar rather than a perspective based main toolbar. Make sure the
   * returned array always contains the same action objects.
   * 
   * @return an array of {@link IAction}s that can build a tree using paths
   *         relative to the windows toolbar
   */
  IAction[] getActions();

  /**
   * Provides the component that is displayed in the generated window. The used
   * window manger should make sure that this method is called from the EDT
   * because Swing is not thread safe. Note: this method might be called more
   * than once due to docking purposes. Make sure you preserve the state while
   * the window exists.
   * 
   * @return the Swing component that is displayed within the generated window
   */
  JComponent getContent();

  /**
   * Defines the way the window is treated and placed within the main window.
   * {@code null} should not be returned because the behavior might be undefined
   * depending on the used {@link IWindowManager}
   * 
   * @return a contribution
   */
  Contribution getContribution();

  /**
   * Provides the window's title
   * 
   * @return window title
   */
  String getTitle();

  /**
   * Specifies whether this window supports undo and redo operations.
   * 
   * @return true if {@link #getUndoManager()} returns an actual
   *         {@link JamesUndoManager}
   */
  boolean isUndoRedoSupported();

  /**
   * If {@link #isUndoRedoSupported()} returns true this function should return
   * an {@link JamesUndoManager} for this window
   * 
   * @return an {@link JamesUndoManager} if {@link #isUndoRedoSupported()}==
   *         {@code true}
   */
  JamesUndoManager getUndoManager();

  /**
   * Provides an {@link Icon} that can be displayed for the window
   * 
   * @return the window's icon
   */
  Icon getWindowIcon();

  /**
   * called when the window changed state from deactivated to activated or was
   * opened and activated
   */
  void windowActivated();

  /**
   * called when the window changes state to deactivated
   */
  void windowDeactivated();

  /**
   * Called when the window manager adds this window to its list of windows. The
   * window manager can return be used to close activate and deactivate the
   * window.
   * 
   * @param manager
   *          the window manager this window belongs to
   */
  void installWindowManager(IWindowManager manager);

  /**
   * Method that is called when the window should be closed. It gives the
   * opportunity to reject the close request by returning false. But should
   * eventually return true.
   * 
   * @return true if the window can be closed at this moment
   * 
   */
  boolean canClose();

  /**
   * Called when the window was closed
   */
  void windowClosed();

  /**
   * Adds a window change listener to the window so that it can be used to
   * notify changes to the window like title, icon or action changes
   * 
   * @param listener
   *          the listener to add
   */
  void addWindowChangeListener(IWindowChangeListener listener);

  /**
   * Removes a previously added window change listener from the window
   * 
   * @param listener
   *          the listener to remove
   */
  void removeWindowChangeListener(IWindowChangeListener listener);

  /**
   * @return true if the window has a save function that is capable of saving
   *         information contained in the window.
   */
  boolean isSaveable();

  /**
   * If {@link #isSaveable()} returns {@code true} this method must be
   * implemented to save whatever needs to be saved for that window.
   */
  void save();

  /**
   * if {@link #isSaveable()} returns true this method must be implemented to
   * save whatever needs to be saved for that window providing a way for the
   * user to define where to save it.
   */
  void saveAs();

  /**
   * Specifies the preferred size of this window. Whether this size is honored
   * or not depends on the contribution type and the window manager.
   * 
   * @return the preferred size of this window
   */
  Dimension getPreferredSize();

  /**
   * Specifies an ID for that {@link IWindow} type, e. g. an editor for model X,
   * by which this {@link IWindow} can be identified as a window belonging to a
   * specific type of windows. This ID in return is used for storing window type
   * specific preferences that may override the preferences originally specified
   * by the {@link IWindow} itself. So for instance it might be that the window
   * manager stores a contribution other than the specified for a specific kind
   * of windows and overrides it using this ID
   * 
   * @return the window type id
   */
  String getWindowID();
}
