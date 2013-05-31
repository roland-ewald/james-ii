/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.treetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreeModel;

/**
 * Wrapper around {@link ITreeTableModel} that handles collapsing and expanding
 * individual nodes of the tree.
 * <p>
 * Basically the {@link ITreeTableModel} just provides a tree and this view
 * model manages the magic necessary to use it in the UI. An instance of
 * {@link ITreeTableModel} can't be used directly as model for a
 * {@link TreeTable}; it will always be wrapped in an instance of this class.
 * 
 * @author Johannes Rössel
 * @author Stefan Rybacki
 */
class TreeTableViewModel extends AbstractTableModel implements
    TreeModelListener, ITreeTableModelListener {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6727200275181055187L;

  /** The wrapped {@link ITreeTableModel}. */
  private ITreeTableModel ttModel;

  // === INTERNAL STATE ===

  /** Cache of depth information for every visible node */
  private Map<Object, Integer> depths;

  /** List of visible nodes, as presented to the {@link JTable}. */
  private List<Object> items;

  /** Set of currently expanded nodes. */
  private Set<Object> expanded;

  // == END INTERNAL STATE ===

  /**
   * The root visible flag.
   */
  private boolean rootVisible;

  /**
   * Initialises an instance of the {@link TreeTableViewModel} class wrapping an
   * instance of {@link ITreeTableModel}.
   * 
   * @param ttModel
   */
  public TreeTableViewModel(ITreeTableModel ttModel) {
    this.ttModel = ttModel;
    init();
  }

  /**
   * Retrieves the view row index of a given node in the tree.
   * 
   * @param node
   *          The tree node.
   * @return The row's view index or −1 if the node isn't visible.
   */
  public int getViewIndex(Object node) {
    for (int i = 0; i < items.size(); i++) {
      if (items.get(i) == node) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Gets the node at a given row index.
   * 
   * @param viewRow
   *          A row view index.
   * @return The associated node in the model, or null if there is no such node.
   */
  public Object getNode(int viewRow) {
    if (viewRow < 0 || viewRow >= items.size()) {
      return null;
    }

    return items.get(viewRow);
  }

  /**
   * Returns the underlying model.
   * 
   * @return The {@link ITreeTableModel} underlying this view model.
   */
  public ITreeTableModel getModel() {
    return ttModel;
  }

  /**
   * Initialises and resets internal caches. This is necessary on first
   * initialisation of the ViewModel and also when the underlying model changes.
   */
  private final void init() {
    depths = new HashMap<>();
    items = new ArrayList<>();
    expanded = new HashSet<>();

    // TODO! Model listener
    ttModel.addTreeTableModelListener(this);

    updateState();
    fireTableDataChanged();
  }

  /**
   * Updates the list of visible nodes.
   */
  private synchronized void updateState() {
    items.clear();
    depths.clear();

    Object root = ttModel.getRoot();

    if (rootVisible) {
      updateState(root, 0);
    } else {
      for (int c = 0; c < ttModel.getChildCount(root); c++) {
        updateState(ttModel.getChild(root, c), 0);
      }
    }
  }

  /**
   * Updates the list of visible nodes.
   * 
   * @param node
   *          The tree node to update. Every visible child below it will be
   *          visited as well.
   * @param depth
   *          the depth of the passed node
   */
  private void updateState(Object node, int depth) {
    if (depths.containsKey(node)) {
      throw new RuntimeException(
          "Tree structure is not allowed to have loops! (Node:" + node + ")");
    }

    items.add(node);
    depths.put(node, depth);
    if (isExpanded(node)) {
      for (int i = 0; i < ttModel.getChildCount(node); i++) {
        // avoid loops in tree structure
        updateState(ttModel.getChild(node, i), depth + 1);
      }
    }
  }

  /**
   * Returns the depth of the given row.
   * 
   * @param row
   *          Row index.
   * @return The depth of the given row.
   */
  public int getDepth(int row) {
    Object node = getNode(row);
    if (node == null) {
      return 0;
    }

    if (depths.get(node) == null) {
      return 0;
    }

    return depths.get(node);
  }

  /**
   * Returns a value indicating whether the given node (row) is expanded or not.
   * An expanded node has its children visible. Leaves (cf.
   * {@link #isLeaf(Object)} ) are never expanded and therefore return
   * {@code false}.
   * 
   * @param node
   *          the node to check
   * @return A value indicating whether the given node is expanded or not.
   */
  public boolean isExpanded(Object node) {
    return expanded.contains(node);
  }

  /**
   * Sets a value indicating the expanded state for a given node (row).
   * 
   * @param node
   *          The node to expand or collapse.
   * @param value
   *          A value indicating whether the node should be expanded or
   *          collapsed. When true, the node will be expanded, when false it
   *          will be collapsed.
   */
  public synchronized void setExpanded(Object node, boolean value) {
    // find node for rowIndex
    if (value) {
      expanded.add(node);
    } else {
      expanded.remove(node);
    }

    // TODO! Make this smarter
    updateState();
    fireTableDataChanged();
  }

  /**
   * Returns a value indicating whether the given node (row) is considered a
   * leaf in the tree or not. Leaves are defined as nodes which cannot have
   * children. However, a non-leaf node might have no children but may get
   * children later. This semantics is mainly continued from the underlying
   * {@link ITreeTableModel} and, through that, {@link TreeModel}.
   * 
   * @param node
   *          the node
   * @return A value indicating whether the given node is a leaf or not.
   */
  public boolean isLeaf(Object node) {
    return ttModel.isLeaf(node);
  }

  @Override
  public int getColumnCount() {
    return ttModel.getColumnCount();
  }

  @Override
  public String getColumnName(int column) {
    return ttModel.getColumnName(column);
  }

  @Override
  public int getRowCount() {
    return items.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return ttModel.getValueAt(items.get(rowIndex), columnIndex);
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return ttModel.getColumnClass(columnIndex);
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return ttModel.isCellEditable(items.get(rowIndex), columnIndex);
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    ttModel.setValueAt(value, items.get(rowIndex), columnIndex);
  }

  /**
   * Sets whether the root of the tree is visible or not. If it is invisible the
   * direct children of the tree root are displayed as top-level entries.
   * 
   * @param rootVisible
   *          The new value of the property.
   */
  public synchronized void setRootVisible(boolean rootVisible) {
    this.rootVisible = rootVisible;
    if (!expanded.contains(ttModel.getRoot())) {
      expanded.add(ttModel.getRoot());
    }
    updateState();
    fireTableDataChanged();
  }

  /**
   * Gets a value that determines visibility of the tree's root. If the root is
   * invisible, its direct children are displayed as top-level entries.
   * 
   * @return A value indicating whether the tree's root is visible or not.
   */
  public synchronized boolean isRootVisible() {
    return rootVisible;
  }

  @Override
  public synchronized void treeNodesChanged(TreeModelEvent e) {
    // TODO
    updateState();
    fireTableDataChanged();
  }

  @Override
  public synchronized void treeNodesInserted(TreeModelEvent e) {
    // TODO
    updateState();
    fireTableDataChanged();
  }

  @Override
  public synchronized void treeNodesRemoved(TreeModelEvent e) {
    // TODO
    updateState();
    fireTableDataChanged();
  }

  @Override
  public synchronized void treeStructureChanged(TreeModelEvent e) {
    // TODO
    updateState();
    fireTableDataChanged();
  }

  @Override
  public synchronized void columnInserted(ITreeTableModel model, int column) {
    if (model.equals(ttModel)) {
      fireTableStructureChanged();
    }
  }

  @Override
  public synchronized void columnRemoved(ITreeTableModel model, int column) {
    if (model.equals(ttModel)) {
      fireTableStructureChanged();
    }
  }
}
