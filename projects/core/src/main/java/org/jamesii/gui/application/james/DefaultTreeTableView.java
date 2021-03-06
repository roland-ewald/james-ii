/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.syntaxeditor.JamesUndoManager;
import org.jamesii.gui.utils.treetable.ITreeTableModel;
import org.jamesii.gui.utils.treetable.TreeTable;

/**
 * Base class that can be used to provide table like views for plugging into the
 * JAMES II GUI. Just provide a table model, a contribution, an icon and
 * override methods like {@link #generateActions()} accordingly to provide
 * custom actions.
 * 
 * @author Stefan Rybacki
 * 
 */
public class DefaultTreeTableView extends AbstractWindow {
  /**
   * the swing table
   */
  private TreeTable table;

  /**
   * Creates a table view for use in JAMES II GUI
   * 
   * @param title
   *          the view title
   * @param model
   *          the table model to use
   * @param contribution
   *          the view contribution
   * @param icon
   *          the view's icon
   */
  public DefaultTreeTableView(String title, ITreeTableModel model,
      Contribution contribution, Icon icon) {
    super(title, icon, contribution);
    table = new TreeTable(model);
    table.setFillsViewportHeight(true);
    setTreeTableModel(model);
  }

  /**
   * Sets a custom table cell render that is used to render the table.
   * 
   * @param columnClass
   *          the class where to use the specified renderer
   * @param renderer
   *          the render to use for given column class
   */
  protected final void setTableRenderer(Class<?> columnClass,
      TableCellRenderer renderer) {
    table.setDefaultRenderer(columnClass, renderer);
  }

  /**
   * sets the table model
   * 
   * @param model
   *          the model
   */
  protected final void setTreeTableModel(ITreeTableModel model) {
    table.setTreeTableModel(model);
  }

  @Override
  public JComponent createContent() {
    return new JScrollPane(table);
  }

  @Override
  public JamesUndoManager getUndoManager() {
    return null;
  }

  @Override
  public boolean isUndoRedoSupported() {
    return false;
  }

  /**
   * Adds a selection listener to the table
   * 
   * @param l
   *          the listener to add
   */
  protected final synchronized void addSelectionListener(ListSelectionListener l) {
    table.getSelectionModel().addListSelectionListener(l);
  }

  /**
   * Removes a previously registered selection listener
   * 
   * @param l
   *          the listener to remove
   */
  protected final synchronized void removeSelectionListener(
      ListSelectionListener l) {
    table.getSelectionModel().removeListSelectionListener(l);
  }

  /**
   * @return the currently selected row
   */
  protected final synchronized int getSelectedRow() {
    return table.getSelectedRow();
  }

  /**
   * @return the currently used table model
   */
  protected final ITreeTableModel getTreeTableModel() {
    return table.getTreeTableModel();
  }

  @Override
  protected IAction[] generateActions() {
    return new IAction[] {};
  }

  /**
   * Sets the given column model
   * 
   * @param columnModel
   *          the column model
   */
  protected final void setColumnModel(TableColumnModel columnModel) {
    table.setColumnModel(columnModel);
  }

  /**
   * Sets the selection mode to use
   * 
   * @param mode
   *          the mode to set
   * @see javax.swing.ListSelectionModel#MULTIPLE_INTERVAL_SELECTION
   * @see javax.swing.ListSelectionModel#SINGLE_INTERVAL_SELECTION
   * @see javax.swing.ListSelectionModel#SINGLE_SELECTION
   */
  protected final void setSelectionMode(int mode) {
    table.setSelectionMode(mode);
  }

  /**
   * @return the table used in the view
   */
  protected final TreeTable getTreeTable() {
    return table;
  }
}
