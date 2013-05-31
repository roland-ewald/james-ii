/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowListener;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.syntaxeditor.IUndoRedoListener;
import org.jamesii.gui.syntaxeditor.JamesUndoManager;
import org.jamesii.gui.syntaxeditor.SyntaxEditor;

/**
 * Simple undo action for a {@link SyntaxEditor} that calls when invoked its
 * {@link JamesUndoManager#undo()} method.
 * 
 * @author Stefan Rybacki
 */
class UndoAction extends AbstractAction implements IUndoRedoListener,
    IWindowListener {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7677032041195927803L;

  /**
   * editor the action is responsible for
   */
  private JamesUndoManager manager = null;

  /**
   * attaches this undo action to another undo manager
   * 
   * @param manager
   *          the manager the action can invoke an undo action on
   */
  public final void setManager(JamesUndoManager manager) {
    if (this.manager != null) {
      this.manager.removeUndoRedoListener(this);
    }

    Object old = this.manager;

    this.manager = manager;
    if (manager != null) {
      manager.addUndoRedoListener(this);
    }

    checkUndoRedoState();

    firePropertyChange("manager", old, manager);
  }

  /**
   * Creates an undo action for specified editor (it also registers as
   * {@link IUndoRedoListener} so it can keep its enabled state up to date
   * 
   * @param manager
   *          the manager the action can invoke an undo action on
   * 
   */
  public UndoAction(JamesUndoManager manager) {
    super("Undo");
    setManager(manager);

    putValue(SMALL_ICON, IconManager.getIcon(IconIdentifier.UNDO_SMALL, "Undo"));
  }

  /**
   * Creates an undo action for specified {@link IWindowManager} (it also
   * registers as {@link IUndoRedoListener} and {@link IWindowListener} so it
   * can keep its enabled state up to date
   * 
   * @param windowManager
   *          the window manager to register at
   */
  public UndoAction(IWindowManager windowManager) {
    this((JamesUndoManager) null);
    if (windowManager != null) {
      windowManager.addWindowListener(this);
    }
  }

  @Override
  public final void actionPerformed(ActionEvent e) {
    if (manager.canUndo()) {
      manager.undo();
    }
  }

  @Override
  public void checkUndoRedoState() {
    setEnabled(manager != null && manager.canUndo());
    if (manager != null) {
      putValue(SHORT_DESCRIPTION, manager.getUndoPresentationName());
    }
  }

  @Override
  public void windowActivated(IWindow window) {
    if (window == null || !window.isUndoRedoSupported()) {
      setManager(null);
    } else {
      setManager(window.getUndoManager());
    }
  }

  @Override
  public void windowClosed(IWindow window) {

  }

  @Override
  public void windowDeactivated(IWindow window) {

  }

  @Override
  public void windowOpened(IWindow window) {

  }

}
