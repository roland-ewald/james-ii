/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.treetable;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.jamesii.gui.utils.AbstractTreeModel;

/**
 * Convenience class that implements the {@link ITreeTableModel} interface using
 * {@link AbstractTreeModel} to provide basic implementation for
 * {@link javax.swing.tree.TreeModel} stuff.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractTreeTableModel extends AbstractTreeModel
    implements ITreeTableModel {

  @Override
  public final synchronized void addTreeTableModelListener(
      ITreeTableModelListener listener) {
    addTreeModelListener(listener);
  }

  @Override
  public final synchronized void removeTreeTableModelListener(
      ITreeTableModelListener listener) {
    removeTreeModelListener(listener);
  }

  /**
   * Fire column inserted.
   * 
   * @param column
   *          the column that was inserted
   */
  protected synchronized void fireColumnInserted(int column) {
    for (TreeModelListener l : getListeners()) {
      if (l instanceof ITreeTableModelListener) {
        ((ITreeTableModelListener) l).columnInserted(this, column);
      }
    }
  }

  /**
   * Fire column removed.
   * 
   * @param column
   *          the column that was removed
   */
  protected synchronized void fireColumnRemoved(int column) {
    for (TreeModelListener l : getListeners()) {
      if (l instanceof ITreeTableModelListener) {
        ((ITreeTableModelListener) l).columnRemoved(this, column);
      }
    }
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return Object.class;
  }

  @Override
  public void setValueAt(Object aValue, Object node, int columnIndex) {
  }

  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
  }

  @Override
  public boolean isCellEditable(Object node, int columnIndex) {
    return false;
  }
}
