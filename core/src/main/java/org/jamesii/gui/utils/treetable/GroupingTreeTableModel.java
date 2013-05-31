/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.treetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

/**
 * Class that wraps a {@link TableModel} to work in a {@link TreeTable} by
 * creating a hierarchy by grouping over values of a specified column of the
 * wrapped model. For instance if the original {@link TableModel} contains rows
 * representing files where there are columns for filename, file size and file
 * type this class can be used to group by file type where e.g., each file of
 * type image is within one branch of the tree.
 * 
 * @author Stefan Rybacki
 */
public class GroupingTreeTableModel extends AbstractTreeTableModel implements
    TableModelListener {

  /**
   * The wrapped table model.
   */
  private TableModel model;

  /**
   * The column to group by.
   */
  private int groupingColumn;

  /**
   * The groups.
   */
  private final List<Object> groups = Collections
      .synchronizedList(new ArrayList<>());

  /**
   * The root node.
   */
  private Object root = new Object();

  /**
   * Mapping from row index of table model to group
   */
  private Map<Object, List<Integer>> rowsGroupMapping = new HashMap<>();

  /**
   * Instantiates a new grouping tree table model.
   * 
   * @param model
   *          the table to wrap and group
   * @param groupingColumn
   *          the grouping column the column by which is grouped (there is also
   *          the customizing option by overriding
   *          {@link #getGroupingObject(Object)} and/or
   *          {@link #getValueForGroup(Object, int)}.
   */
  public GroupingTreeTableModel(TableModel model, int groupingColumn) {
    super();
    this.model = model;
    this.groupingColumn = groupingColumn;
    if (model == null) {
      throw new IllegalArgumentException("model can't be null");
    }
    model.addTableModelListener(this);
    refreshGroups();
  }

  @Override
  public final int getColumnCount() {
    return model.getColumnCount();
  }

  @Override
  public final void tableChanged(TableModelEvent e) {
    // TODO sr137: react on individual table events
    refreshGroups(); // NOSONAR: one could introduce an extra grouping object
                     // provider interface but it's not needed here
    fireTreeNodesChanged(this, new Object[] { getRoot() }, null, null);
    // if something was removed check for obsolete groups
    // if something was added check for new groups
    // if everything changed rebuild groups
  }

  /**
   * Helper method that is used to refresh available groups as well as to
   * refresh the view to model mapping.
   */
  private void refreshGroups() {
    groups.clear();
    rowsGroupMapping = new HashMap<>();

    // get all groups and store links to row indices for each group
    for (int i = 0; i < model.getRowCount(); i++) {
      Object groupingObject;
      if (groupingColumn < 0 || groupingColumn >= getColumnCount()) {
        groupingObject = new String("No Grouping"); // NOSONAR: needs to be new
                                                    // String
      } else {
        groupingObject = getGroupingObject(model.getValueAt(i, groupingColumn));
      }
      if (!groups.contains(groupingObject)) {
        groups.add(groupingObject);
      }

      List<Integer> list = rowsGroupMapping.get(groupingObject);
      if (list == null) {
        list = new ArrayList<>();
      }

      list.add(Integer.valueOf(i));
      rowsGroupMapping.put(groupingObject, list);
    }

  }

  /**
   * Gets the row index in the wrapped {@link TableModel} for the specified
   * node.
   * 
   * @param node
   *          the node
   * @return the row index in the wrapped {@link TableModel} or -1 if not in the
   *         {@link TableModel}
   */
  public int getRowForNode(Object node) {
    if (node instanceof Integer) {
      return (Integer) node;
    }
    return -1;
  }

  /**
   * This method is used to retrieve values for each column of the table for
   * grouping nodes that are used to group the original table entries. Those
   * values are inserted automatically but can be customized using this method.
   * This way it is possible to provide custom elements for each column that are
   * shown for grouping nodes. The provided grouping object is the object that
   * is equal for each element in the group in the specified grouping column.
   * <p/>
   * Beware: inserted grouping object will be one of the objects returned by
   * {@link #getGroupingObject(Object)}
   * 
   * @param groupingObject
   *          the grouping object contained in all subsequent entries in the
   *          specified grouping column
   * @param columnIndex
   *          the column index (not the grouping column index!)
   * @return the value for the group in the specified column
   */
  protected Object getValueForGroup(Object groupingObject, int columnIndex) {
    switch (columnIndex) {
    case 0:
      return groupingObject;
    default:
      return null;
    }
  }

  /**
   * Gets the grouping object derived from the original object that was
   * contained in the grouping column. The default implementation just returns
   * the same object so that grouping is based on the object alone. The idea of
   * this method is that one might be calculating a different grouping object
   * and therefore a different grouping criteria based on the original object in
   * the grouping column. E.g., this makes it possible to merge different groups
   * into one if needed.
   * 
   * @param objectInGroupingColumn
   *          the object in grouping column
   * @return the grouping object
   */
  protected Object getGroupingObject(Object objectInGroupingColumn) {
    return objectInGroupingColumn;
  }

  @Override
  public final Object getValueAt(Object node, int column) {
    if (node == root) { // NOSONAR
      return null;
    }
    if (node instanceof Group) {
      return this.getValueForGroup(((Group) node).getGroupingObject(), column);
    }

    if (node instanceof Integer) {
      return model.getValueAt((Integer) node, column);
    }

    return null;
  }

  @Override
  public boolean isCellEditable(Object node, int columnIndex) {
    return false;
  }

  @Override
  public void setValueAt(Object value, Object node, int columnIndex) {
  }

  @Override
  public final Object getChild(Object parent, int index) {
    if (parent == root) { // NOSONAR
      return new Group(groups.get(index));
    }

    if (parent instanceof Group) {
      List<Integer> list =
          rowsGroupMapping.get(((Group) parent).getGroupingObject());
      if (list == null) {
        return null;
      }
      return list.get(index);
    }
    return null;
  }

  @Override
  public final int getChildCount(Object parent) {
    if (parent == root) { // NOSONAR
      return groups.size();
    }

    if (parent instanceof Group) {
      List<Integer> list =
          rowsGroupMapping.get(((Group) parent).getGroupingObject());
      if (list == null) {
        return 0;
      }
      return list.size();
    }

    return 0;
  }

  @Override
  public final int getIndexOfChild(Object parent, Object child) {
    if (parent == root) {
      return groups.indexOf(child);
    }

    if (parent instanceof Group) {
      List<Integer> list =
          rowsGroupMapping.get(((Group) parent).getGroupingObject());
      if (list == null) {
        return -1;
      }
      return list.indexOf(child);
    }
    return 0;
  }

  @Override
  public final Object getRoot() {
    return root;
  }

  @Override
  public final boolean isLeaf(Object node) {
    return !(node == root || node instanceof Group); // NOSONAR
  }

  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
  }

  @Override
  public final Class<?> getColumnClass(int columnIndex) {
    return model.getColumnClass(columnIndex);
  }

  @Override
  public final String getColumnName(int columnIndex) {
    return model.getColumnName(columnIndex);
  }

  /**
   * @return the wrapped table model
   */
  public final TableModel getModel() {
    return model;
  }

  /**
   * Gets the element count in a group specified by grouping object.
   * 
   * @param groupingObject
   *          the grouping object
   * @return the element count in group
   */
  public final int getElementCountInGroup(Object groupingObject) {
    List<Integer> list = rowsGroupMapping.get(groupingObject);
    if (list == null) {
      return 0;
    }
    return list.size();
  }

  /**
   * Represents a grouping row (basically a grouping node in the tree for the
   * specified grouping object)
   */
  private static class Group {

    /**
     * The grouping object.
     */
    private Object groupingObject;

    /**
     * Instantiates a new group.
     * 
     * @param groupingObject
     *          the grouping object
     */
    public Group(Object groupingObject) {
      this.groupingObject = groupingObject;
    }

    /**
     * @return the groupingObject
     */
    public Object getGroupingObject() {
      return groupingObject;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Group) {
        Group group = (Group) obj;
        if (group.getGroupingObject() == groupingObject) {
          return true;
        }

        if (groupingObject != null) {
          return groupingObject.equals(group.getGroupingObject());
        }

        return group.getGroupingObject().equals(groupingObject);
      }

      return false;
    }

    @Override
    public int hashCode() {
      if (groupingObject == null) {
        return Void.class.hashCode();
      }
      return groupingObject.hashCode();
    }

    @Override
    public String toString() {
      return groupingObject == null ? "" : groupingObject.toString();
    }

  }

}
