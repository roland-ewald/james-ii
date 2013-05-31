/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.treetable;

import javax.swing.event.TreeModelListener;

/**
 * Extension of {@link TreeModelListener} that adds some more events specific to
 * the {@link TreeTable}.
 * 
 * @author Stefan Rybacki
 */
public interface ITreeTableModelListener extends TreeModelListener {

  /**
   * Fired when a column was inserted into the tree table model.
   * 
   * @param model
   *          the model the column was inserted into
   * @param column
   *          the column that was inserted
   */
  void columnInserted(ITreeTableModel model, int column);

  /**
   * Fired when a column was removed from the tree table model.
   * 
   * @param model
   *          the model the column was removed from
   * @param column
   *          the column that was removed
   */
  void columnRemoved(ITreeTableModel model, int column);
}
