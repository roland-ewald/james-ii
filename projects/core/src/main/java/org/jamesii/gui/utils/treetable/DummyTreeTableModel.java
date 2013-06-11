/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jamesii.gui.utils.treetable;

/**
 * @author Stefan Rybacki
 */
class DummyTreeTableModel extends AbstractTreeTableModel {
  private static final Object ROOT="root";
  
  @Override
  public Object getRoot() {
    return ROOT;
  }

  @Override
  public Object getChild(Object parent, int index) {
    if (ROOT.equals(parent)) {
      switch (index) {
        case 0: return "A";
        case 1: return "B";
        case 2: return "C";
      }
    }
    return null;
  }

  @Override
  public int getChildCount(Object parent) {
    if (ROOT.equals(parent)) {
      return 3;
    }
    return 0;
  }

  @Override
  public boolean isLeaf(Object node) {
    return !ROOT.equals(node);
  }

  @Override
  public int getIndexOfChild(Object parent, Object child) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public Object getValueAt(Object node, int column) {
    switch (column) {
      case 0: return String.valueOf(node);
      case 1: return String.valueOf(node).length();
    }
    
    return null;
  }

  @Override
  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
      case 0: return "value";
      case 1: return "length";
    }
    
    return null;
  }
  
}
