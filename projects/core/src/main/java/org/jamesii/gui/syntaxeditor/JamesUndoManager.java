/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.jamesii.core.util.collection.ListenerSupport;

/**
 * Custom UndoManager that provides the ability to attach
 * {@link IUndoRedoListener} listeners to it to react on incoming
 * {@link UndoableEdit}s.
 * 
 * @author Stefan Rybacki
 */
public class JamesUndoManager extends javax.swing.undo.UndoManager {
  /**
   * Custom {@link UndoableEdit} that is mutable that means that the
   * significance value can be changed according to grouping purposes.
   * 
   * @author Stefan Rybacki
   */
  static final class MutableUndoableEdit implements UndoableEdit {

    /**
     * the original edit to wrap
     */
    private UndoableEdit edit;

    /**
     * custom significance value
     */
    private boolean significant;

    /**
     * Instantiates a new mutable undoable edit.
     * 
     * @param edit
     *          the edit
     * @param significant
     *          the significance
     */
    private MutableUndoableEdit(UndoableEdit edit, boolean significant) {
      this.edit = edit;
      this.significant = significant;
    }

    /**
     * Sets the significance.
     * 
     * @param s
     *          true if significant
     */
    public final void setSignificant(boolean s) {
      significant = s;
    }

    @Override
    public final boolean isSignificant() {
      return significant;
    }

    @Override
    public void undo() {
      edit.undo();
    }

    @Override
    public void redo() {
      edit.redo();
    }

    @Override
    public boolean canUndo() {
      return edit.canUndo();
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
      return edit.addEdit(anEdit);
    }

    @Override
    public boolean canRedo() {
      return edit.canRedo();
    }

    @Override
    public void die() {
      edit.die();
    }

    @Override
    public String getPresentationName() {
      return edit.getPresentationName();
    }

    @Override
    public String getRedoPresentationName() {
      return edit.getRedoPresentationName();
    }

    @Override
    public String getUndoPresentationName() {
      return edit.getUndoPresentationName();
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
      return edit.replaceEdit(anEdit);
    }
  }

  /** Serialization ID. */
  private static final long serialVersionUID = -5249529211486322977L;

  /** stores all registered IUndoRedoListeners. */
  private ListenerSupport<IUndoRedoListener> undoRedoListeners =
      new ListenerSupport<>();

  /**
   * the time in milliseconds when the last insert occured
   */
  private long lastInsert = 0;

  /** The last edit. */
  private MutableUndoableEdit lastEdit = null;

  /**
   * flag indicating whether to group edits or not
   */
  private boolean groupEdits;

  /**
   * the period of time in milliseconds in which edits are groups
   */
  private int period;

  @Override
  public void undoableEditHappened(UndoableEditEvent e) {
    // TODO sr137: not completely working yet
    if (groupEdits && System.currentTimeMillis() - lastInsert < period
        && lastEdit != null) {
      lastEdit.setSignificant(false);
    } else {
      lastInsert = System.currentTimeMillis();
    }

    lastEdit =
        new MutableUndoableEdit(e.getEdit(), e.getEdit().isSignificant());
    addEdit(lastEdit);
  }

  /**
   * Instantiates a new undo manager.
   */
  public JamesUndoManager() {
    this(false, 0);
  }

  /**
   * Instantiates a new undo manager.
   * 
   * @param groupEdits
   *          true if the manager should group edits (not completely working
   *          yet)
   * @param period
   *          the time between edits in which edits are grouped
   */
  private JamesUndoManager(boolean groupEdits, int period) {
    super();
    setLimit(500);
    this.groupEdits = groupEdits;
    this.period = period;
  }

  /**
   * helper function that notifies listeners to check there undo redo state.
   */
  private synchronized void notifyListeners() {
    for (IUndoRedoListener l : undoRedoListeners.getListeners()) {
      if (l != null) {
        l.checkUndoRedoState();
      }
    }
  }

  @Override
  public synchronized boolean addEdit(UndoableEdit anEdit) {
    boolean res = super.addEdit(anEdit);
    if (res) {
      notifyListeners();
    }
    return res;
  }

  /**
   * Adds a undo redo listener that listens to changes to the canUndo and
   * canRedo states of the editor
   * 
   * @param listener
   *          the listener to add
   */
  public final void addUndoRedoListener(IUndoRedoListener listener) {
    undoRedoListeners.addListener(listener);
  }

  /**
   * Removes a previously attached listener
   * 
   * @param listener
   *          the listener to remove
   */
  public final void removeUndoRedoListener(IUndoRedoListener listener) {
    undoRedoListeners.removeListener(listener);
  }

  @Override
  public synchronized void undo() {
    super.undo();
    notifyListeners();
  }

  @Override
  public synchronized void redo() {
    super.redo();
    notifyListeners();
  }

  @Override
  public synchronized void end() {
    super.end();
    notifyListeners();
  }

  @Override
  public void die() {
    super.die();
    notifyListeners();
  }
}
