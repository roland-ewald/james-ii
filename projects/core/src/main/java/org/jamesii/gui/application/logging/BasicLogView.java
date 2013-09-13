/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jamesii.core.util.logging.ILogListener;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.action.ToggleAction;
import org.jamesii.gui.application.james.DefaultTreeTableView;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.utils.treetable.GroupingTreeTableModel;

/**
 * View implementing {@link org.jamesii.gui.application.IWindow} that provides
 * access to log records logged by an
 * {@link org.jamesii.core.util.logging.ApplicationLogger}. There is a
 * specialized version of this class for the local ApplicationLogger
 * {@link LogView}.
 * 
 * @author Stefan Rybacki
 */

public class BasicLogView extends DefaultTreeTableView implements ILogListener {

  /**
   * renderer used in table
   */
  private static final TableCellRenderer renderer = new LevelAwareCellRenderer(
      IconManager.getIcon(IconIdentifier.ERROR_SMALL, "x"),
      IconManager.getIcon(IconIdentifier.WARNING_SMALL, "!"),
      IconManager.getIcon(IconIdentifier.INFO_SMALL, "i"),
      IconManager.getIcon(IconIdentifier.WIZARD_SMALL), null);

  /**
   * Custom {@link javax.swing.table.TableColumnModel} for the log table
   * 
   * @author Stefan Rybacki
   */
  private static class LogRecordTableColumnModel extends
      DefaultTableColumnModel {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 7004673459245514365L;

    @Override
    public TableColumn getColumn(int columnIndex) {
      TableColumn c = super.getColumn(columnIndex);
      c.setCellRenderer(renderer);
      switch (columnIndex) {
      case 0:
        c.setCellRenderer(renderer);
        c.setMaxWidth(60);
        break;
      case 1:
      case 2:
        c.setMaxWidth(75);
        break;
      }
      return c;
    }
  }

  /**
   * the log table's model
   */
  private LogRecordTableModel model;

  /**
   * The tree table model.
   */
  private GroupingTreeTableModel treeTableModel;

  /**
   * Instantiates a new basic log view.
   * 
   * @param title
   *          the title
   * @param contribution
   *          the contribution
   * @param icon
   *          the icon
   */
  public BasicLogView(String title, Contribution contribution, Icon icon) {
    super(title, new GroupingTreeTableModel(new LogRecordTableModel(), 0) {

      @Override
      protected Object getValueForGroup(Object groupingObject, int columnIndex) {
        if (columnIndex == 3) {
          // add log count
          return String.format("(%d) %s elements",
              this.getElementCountInGroup(groupingObject),
              groupingObject.toString());
        }

        return super.getValueForGroup(groupingObject, columnIndex);
      }

    }, contribution, icon);
    treeTableModel = (GroupingTreeTableModel) getTreeTableModel();
    model = (LogRecordTableModel) (treeTableModel).getModel();
    initModel();
  }

  /**
   * helper method that initiates the log table
   */
  private void initModel() {
    setColumnModel(new LogRecordTableColumnModel());
    setTreeTableModel(treeTableModel);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    getTreeTable().setRowHeight(20);
    getTreeTable().getTableHeader().setReorderingAllowed(false);
    getTreeTable().getTableHeader().setResizingAllowed(false);
  }

  @Override
  public synchronized void publish(final LogRecord record) {
    model.addRecord(record);
    /*
     * if (model.isFlat()) BasicUtilities.invokeLaterOnEDT(new Runnable() {
     * 
     * @Override public void run() { // scroll down
     * getTreeTable().changeSelection( getTreeTable().getRowCount() - 1,
     * getTreeTable().getRowCount() - 1, false, false);
     * getTreeTable().scrollRectToVisible( getTreeTable().getCellRect(
     * getTreeTable().getRowCount() - 1, 1, true)); } });
     */
  }

  @Override
  protected IAction[] generateActions() {
    Icon clearIcon = null;
    clearIcon = IconManager.getIcon(IconIdentifier.DELETE_SMALL, "Clear Log");

    IAction clearAction =
        new AbstractAction("log.clear", "Clear Log", clearIcon,
            new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            synchronized (this) {
              model.clear();
            }
          }
        };

    IAction flatAction =
        new ToggleAction("org.jamesii.logview.toggle", "Flat",
            IconManager.getIcon(IconIdentifier.FLAT_SMALL),
            new String[] { "?last" }, null, null, this) {

          /**
           * The flat icon.
           */
          @SuppressWarnings("unused")
          private final Icon flatIcon = IconManager
              .getIcon(IconIdentifier.FLAT_SMALL);

          /**
           * The hierarchical icon.
           */
          @SuppressWarnings("unused")
          private final Icon hierarchicalIcon = IconManager
              .getIcon(IconIdentifier.HIERARCHICAL_SMALL);

          @Override
          protected void toggleChanged(boolean previousState) {
            /*
             * model.setFlat(!previousState); if (previousState) {
             * this.setLabel("Flat"); this.setIcon(flatIcon); } else {
             * this.setLabel("Hierarchical"); this.setIcon(hierarchicalIcon); }
             */

          }
        };

    flatAction.setEnabled(false);

    Icon exceptionIcon = null;
    exceptionIcon = IconManager.getIcon(IconIdentifier.ERROR_SMALL, null);

    final IAction showThrownAction =
        new AbstractAction("log.showthrown", "Show Exception Stack Trace",
            exceptionIcon, new String[] { "" }, null, null, this) {

          @Override
          public void execute() {
            Object node = getTreeTable().getNode(getSelectedRow());
            if (node instanceof Integer) {
              LogRecord record = model.getRecord((Integer) node);
              StringWriter result = new StringWriter();
              PrintWriter writer = new PrintWriter(result);

              if (record.getThrown() != null) {
                record.getThrown().printStackTrace(writer);
                JOptionPane.showMessageDialog(WindowManagerManager
                    .getWindowManager().getMainWindow(), result.toString());
              }
            }
          }
        };
    showThrownAction.setEnabled(false);

    addSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        showThrownAction.setEnabled(false);
        if (getSelectedRow() < 0
            || getSelectedRow() >= getTreeTable().getModel().getRowCount()) {
          return;
        }

        int row =
            treeTableModel.getRowForNode(getTreeTable().getNode(
                getSelectedRow()));
        if (row >= 0) {
          LogRecord record = model.getRecord(row);
          showThrownAction.setEnabled(record != null
              && record.getThrown() != null);
        }

      }

    });

    return new IAction[] { clearAction, showThrownAction, flatAction };
  }

  @Override
  public boolean canClose() {
    return true;
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.view.basiclog";
  }

  @Override
  public void flush() {
  }

}
