/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * A table that provides access to currently managed {@link ITask}s within the
 * {@link TaskManager}. Where the used table model is
 * {@link TaskManagerTableModel}.
 * 
 * @author Stefan Rybacki
 */
public class TaskManagerTable extends JTable {

  /** Serialization ID. */
  private static final long serialVersionUID = -6030325185967007908L;

  /** The progress renderer. */
  private ProgressTableCellRenderer progressRenderer =
      new ProgressTableCellRenderer();

  /** The boolean renderer. */
  private BooleanTableCellRenderer booleanRenderer =
      new BooleanTableCellRenderer();

  /**
   * Instantiates a new task manager table.
   */
  public TaskManagerTable() {
    super();
    setDefaultRenderer(IProgress.class, progressRenderer);
    setDefaultRenderer(Boolean.class, booleanRenderer);
    setRowSelectionAllowed(true);
    setColumnSelectionAllowed(false);
    getTableHeader().setReorderingAllowed(false);
    getTableHeader().setResizingAllowed(false);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    setColumnModel(new TaskManagerTableColumnModel());
    super.setModel(TaskManagerTableModel.getInstance());

  }

  @Override
  public void setModel(TableModel dataModel) {
  }

  @Override
  public void updateUI() {
    super.updateUI();
    if (progressRenderer != null) {
      progressRenderer.updateUI();
    }
    if (booleanRenderer != null) {
      booleanRenderer.updateUI();
    }
  }

  /**
   * Private helper class that provides a
   * {@link javax.swing.table.TableColumnModel} for the {@link TaskManagerTable}
   * , where it basically is only responsible for setting fixed column widths.
   * 
   * @author Stefan Rybacki
   */
  private static class TaskManagerTableColumnModel extends
      DefaultTableColumnModel {

    /** Serialization ID. */
    private static final long serialVersionUID = 7004673459245514366L;

    @Override
    public TableColumn getColumn(int columnIndex) {
      TableColumn c = super.getColumn(columnIndex);
      switch (columnIndex) {
      case TaskManagerTableModel.BLOCKING_COLUMN:
        c.setMinWidth(75);
        c.setMaxWidth(75);
        break;
      case TaskManagerTableModel.PROGRESS_COLUMN:
        c.setMinWidth(250);
        c.setMaxWidth(250);
        break;
      case TaskManagerTableModel.NAME_COLUMN:
        c.setMinWidth(250);
        c.setMaxWidth(250);
        break;
      }
      return c;
    }
  }

}
