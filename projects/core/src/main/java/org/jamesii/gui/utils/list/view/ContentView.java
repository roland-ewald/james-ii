/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jamesii.gui.utils.list.IViewableItem;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class ContentView extends BasicTableView {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = -7720927753191876061L;

  /**
   * The data to be shown in the view.
   */
  private TableModel data;

  public ContentView(IViewableItem[] itemData) {
    super();

    // setDefaultRenderer(String.class, new BorderLessRenderer());
    // setDefaultRenderer(Icon.class, new BorderLessRenderer());

    setShowHorizontalLines(true);
    setShowVerticalLines(false);

    // setRowHeight(getRowHeight() * 2);
    setRowHeight(IViewableItem.TILES.width + 8);

    data = new TableData(itemData);

    setModel(this.data);

    // setRowSelectionAllowed(true);
    // setCellSelectionEnabled(false);

    // setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    // getColumnModel().getColumn(0).setPreferredWidth(IViewableItem.TILES.width+2);
    getColumnModel().getColumn(0).setResizable(false);
    getColumnModel().getColumn(0).setMaxWidth(IViewableItem.TILES.width + 8);
  }

  // Implement table cell tool tips.
  @Override
  public String getToolTipText(MouseEvent e) {
    String tip = null;
    java.awt.Point p = e.getPoint();
    int rowIndex = rowAtPoint(p);
    int colIndex = columnAtPoint(p);
    // int realColumnIndex = convertColumnIndexToModel(colIndex);

    tip = data.getValueAt(rowIndex, colIndex) + "";

    if (tip.equals("")) {
      return null;
    }

    // this line makes a collection (of comma sep values) appear as a multiline
    // block
    tip = tip.replace(",", "<p>");

    return "<html>" + tip + "</html>";
  }

  /**
   * 
   * @author Jan Himmelspach
   * 
   */
  public class TableData implements TableModel, Serializable {

    /**
     * The constant serial version uid.
     */
    private static final long serialVersionUID = 6746322366024183151L;

    private IViewableItem[] data;

    public TableData(IViewableItem[] itemData) {
      data = itemData;
    }

    @Override
    public int getRowCount() {
      return data.length;
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
      return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0) {
        return Icon.class;
      }
      return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

      if (columnIndex == 0) {
        return data[rowIndex].getIcon(IViewableItem.TILES);
      }

      if (columnIndex == 1) {
        return "<html>" + data[rowIndex].getLabel()
            + "<br><font color=\"#C0C0C0\">Type:</font> "
            + data[rowIndex].getType() + "</html>";
      }

      return "<html>"
          // + "<font color=\"#C0C0C0\">Version:</font> " + data[rowIndex].
          + "<br><font color=\"#C0C0C0\">Size:</font> "
          + data[rowIndex].getSize() + "</html>";

    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      // TODO Auto-generated method stub

    }

    @Override
    public void addTableModelListener(TableModelListener l) {
      // TODO Auto-generated method stub

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
      // TODO Auto-generated method stub

    }
  }

  // private class BorderLessRenderer extends DefaultTableCellRenderer {
  //
  // @Override
  // public Component getTableCellRendererComponent(JTable table, Object value,
  // boolean isSelected, boolean hasFocus, int row, int column) {
  //
  // super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
  // row, column);
  //
  // this.setBorder(BorderFactory.createEmptyBorder());
  // return this;
  // }
  // }

}
