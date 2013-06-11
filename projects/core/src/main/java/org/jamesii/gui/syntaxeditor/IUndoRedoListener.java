/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

/**
 * Listener interface for listeners to attach to {@link JamesUndoManager} to
 * listen to undo redo changes.
 * 
 * @author Stefan Rybacki
 */
public interface IUndoRedoListener {
  /**
   * Called by {@link JamesUndoManager} whenever
   * {@link JamesUndoManager#canRedo()} or {@link JamesUndoManager#canUndo()} or
   * {@link JamesUndoManager#canUndoOrRedo()} change
   */
  void checkUndoRedoState();
}
