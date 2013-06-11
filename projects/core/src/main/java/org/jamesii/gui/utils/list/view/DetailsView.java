/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.utils.list.IViewableItem;
import org.jamesii.gui.utils.list.view.item.IProperty;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class DetailsView extends BasicTableView {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = -7720927753191876061L;

  /**
   * the data used in the table
   */
  private TableData data;

  private JPopupMenu popup;

  private Set<IProperty<?>> properties;

  public DetailsView(IViewableItem[] itemData, String[] propertiesToShow) {
    super();

    setShowHorizontalLines(false);
    setShowVerticalLines(false);

    data =
        new TableData(itemData, propertiesToShow == null ? null
            : Arrays.copyOf(propertiesToShow, propertiesToShow.length));

    // Set<Class<IViewableItem>> itemClasses = new
    // HashSet<Class<IViewableItem>>();

    properties = new HashSet<>();

    for (IViewableItem item : itemData) {

      // if (!itemClasses.contains(itemData)) {
      properties.addAll(item.getProperties());
      // }

    }

    popup = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem("Setup columns");
    popup.add(menuItem);

    menuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        List<IProperty<?>> propList = new ArrayList<>(properties);

        Collections.sort(propList, new Comparator<IProperty<?>>() {

          @Override
          public int compare(IProperty<?> o1, IProperty<?> o2) {
            return o1.getName().compareTo(o2.getName());
          }

        });

        PropertySelectionDialog psd =
            new PropertySelectionDialog(
                WindowManagerManager.getWindowManager() != null ? WindowManagerManager
                    .getWindowManager().getMainWindow() : null,
                "Select columns", propList, data.getProperties());

        psd.setVisible(true);
        if (psd.getApply()) {
          String[] props = new String[psd.getSelectedItems().size()];
          int c = 0;
          for (IProperty<?> prop : psd.getSelectedItems()) {
            props[c] = prop.getName();
            c++;
          }

          // TODO keep sort order of the columns, if possible

          if (psd.getSelectedItems().size() == 0) {

            data.setProperties(new String[] { properties.iterator().next()
                .getName() });
          } else {
            data.setProperties(props);
          }

          tableChanged(new TableModelEvent(dataModel,
              TableModelEvent.HEADER_ROW));
        }

      }

    });

    getTableHeader().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          // JTable source = (JTable)e.getSource();
          // int row = source.rowAtPoint( e.getPoint() );
          // int column = source.columnAtPoint( e.getPoint() );

          // if (! source.isRowSelected(row))
          // source.changeSelection(row, column, false, false);

          popup.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    setModel(this.data);
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
  public class TableData implements TableModel {

    /**
     * The data to the displayed.
     */
    private IViewableItem[] data;

    /**
     * The properties to be displayed.
     */
    private String[] properties;

    public TableData(IViewableItem[] itemData, String[] propertiesToShow) {
      data = itemData;
      properties = propertiesToShow;
    }

    @Override
    public int getRowCount() {
      return data.length;
    }

    @Override
    public int getColumnCount() {
      return properties == null ? 1 : properties.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
      if (properties == null) {
        return "Name";
      }
      return properties[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (properties == null) {
        return data[rowIndex].getLabel();
      }
      return data[rowIndex].getProperty(properties[columnIndex]);
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

    public void setProperties(String[] properties) {
      this.properties = properties;
    }

    public String[] getProperties() {
      return properties;
    }

  }

}
