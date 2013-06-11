/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IEditor;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;

/**
 * Searches a suitable editor for a cell in the parameter table.
 * 
 * Created on June 8, 2004
 * 
 * @author Roland Ewald
 */
public class ParameterValueEditor extends AbstractCellEditor implements
    TableCellEditor, ActionListener {

  /** Action to open separate editor. */
  static final String editSeparatelyAction = "editSeperately";

  /** Serialisation ID. */
  static final long serialVersionUID = 7380707401889520062L;

  /** Panel that holds all buttons to delete/add/etc the current parameter. */
  private JPanel buttonPanel = new JPanel();

  /** Reference to editor of the current parameter. */
  private IEditor<?> currentEditor;

  /** Reference to current parameter object. */
  private IEditable<?> currentEditable;

  /** Icon to delete current parameter. */
  private JButton deleteIcon = new JButton("[X]");

  /** Reference to the dialog object. */
  private IPropertyEditor dialogController = null;

  /** Panel in which the editor of the parameter will be shown. */
  private JPanel editorPanel = new JPanel();

  /**
   * Panel that holds buttonPanel and editorPanel for an embedded view in the
   * parameter table.
   */
  private JPanel editPanel = new JPanel();

  /** Icon to use an advanced editor. */
  private JButton openSeperateEditorIcon = new JButton("[...]");

  /** Panel that holds the separate editor in the parameter editing dialog. */
  private JPanel separateEditorPanel = new JPanel();

  /**
   * Default constructor.
   * 
   * @param pdc
   *          the dialog, to request table repaints
   */
  public ParameterValueEditor(IPropertyEditor pdc) {
    dialogController = pdc;
    initializeUIComponents();
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    // Edit parameter
    if (editSeparatelyAction.equals(e.getActionCommand())) {
      JDialog d = new JDialog();
      d.setTitle("Edit '" + currentEditable.getName() + "'");
      d.getContentPane().add(separateEditorPanel);
      d.setSize(separateEditorPanel.getPreferredSize());
      d.setModal(true);
      d.setVisible(true);
    }

  }

  /**
   * Return value to table model.
   * 
   * @return Object value
   */
  @Override
  public Object getCellEditorValue() {
    return currentEditor.getValue();
  }

  /**
   * Creates GUI elements to edit this parameter.
   * 
   * @return Component parameter editor panel
   */
  public Component getParameterEditor() {

    currentEditor = dialogController.getEditor(currentEditable);

    JComponent separateEditor = currentEditor.getSeparateEditorComponent();
    JComponent embeddedEditor = currentEditor.getEmbeddedEditorComponent();

    // Only show icon to open the separate editor if there is one
    if (separateEditor != null) {
      separateEditorPanel.removeAll();
      separateEditorPanel.add(separateEditor, BorderLayout.CENTER);
      openSeperateEditorIcon.setVisible(true);
    } else {
      openSeperateEditorIcon.setVisible(false);
    }

    if (currentEditable.isDeletable()) {
      deleteIcon.setVisible(true);
    } else {
      deleteIcon.setVisible(false);
    }

    editorPanel.removeAll();

    // Only display embedded editor if there is one
    if (embeddedEditor != null) {
      editorPanel.add(embeddedEditor, BorderLayout.CENTER);
    } else {
      editorPanel.add(new JPanel(), BorderLayout.CENTER);
    }

    // a parameter that is edited is also selected
    currentEditor.setEditing(true);
    editPanel.setToolTipText(currentEditable.getDocumentation());

    return editPanel;
  }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object param,
      boolean isSelected, int row, int column) {
    currentEditable = (IEditable<?>) param;
    return getParameterEditor();
  }

  /**
   * Initialisation of UI components.
   */
  public void initializeUIComponents() {

    // Register for event listening

    // parameter actions
    openSeperateEditorIcon.setActionCommand(editSeparatelyAction);
    openSeperateEditorIcon.addActionListener(this);

    // Configuring components for parameter view
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttonPanel.add(openSeperateEditorIcon);
    buttonPanel.add(deleteIcon);

    editorPanel.setLayout(new BorderLayout());
    separateEditorPanel.setLayout(new BorderLayout());

    editPanel.setOpaque(true);
    editPanel.setLayout(new BorderLayout());
    editPanel.add(editorPanel, BorderLayout.CENTER);
    editPanel.add(buttonPanel, BorderLayout.EAST);

  }

}
