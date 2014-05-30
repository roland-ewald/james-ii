/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.treetable;

import javax.swing.tree.TreeModel;

public interface ITreeTableModel extends TreeModel {
  int getColumnCount();

  Object getValueAt(Object node, int column);

  void setValueAt(Object aValue, Object node, int columnIndex);

  String getColumnName(int columnIndex);

  Class<?> getColumnClass(int columnIndex);

  boolean isCellEditable(Object node, int columnIndex);

  void addTreeTableModelListener(ITreeTableModelListener listener);

  void removeTreeTableModelListener(ITreeTableModelListener listener);
}
