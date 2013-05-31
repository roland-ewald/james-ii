/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;

/**
 * Renders the value column of the dialog editor.
 * 
 * @author Roland Ewald
 */
public class ValueCellRenderer implements TableCellRenderer {

  /** Dialog controller. */
  private IPropertyEditor dController;

  /**
   * Default constructor.
   * 
   * @param dialogController
   *          the parameter dialog controller
   */
  public ValueCellRenderer(IPropertyEditor dialogController) {
    dController = dialogController;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table,
      Object parameter, boolean isSelected, boolean hasFocus, int row,
      int column) {
    return dController.getEditor((IEditable<?>) parameter)
        .getEmbeddedEditorComponent();
  }

}
