/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Provides a {@link TreeModel} wrapper that provides sorted access to nodes of
 * another {@link TreeModel}. <b>Note:</b> Asynchronously loaded
 * {@link TreeModel}s might not behave as expected due to the fact, that this
 * model simply iterates through all nodes to create a sorting cache.
 * 
 * @author Stefan Rybacki
 */
public class SortedTreeModel extends AbstractTreeModel {
  /**
   * the tree model to sort from
   */
  private TreeModel treeModel;

  /**
   * The sorting cache.
   */
  private Map<Object, List<Object>> sortingCache = new ConcurrentHashMap<>();

  /**
   * The comparator.
   */
  private Comparator<Object> comparator = new Comparator<Object>() {

    @Override
    public int compare(Object o1, Object o2) {
      if (o1 == o2) {
        return 0;
      }

      if (o1 == null) {
        return -1;
      }
      if (o2 == null) {
        return -1;
      }

      return o1.toString().compareTo(o2.toString());
    }

  };

  /**
   * The listener.
   */
  private TreeModelListener listener = new TreeModelListener() {

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
      // get sorted indices
      Object parent = e.getTreePath().getLastPathComponent();
      SortedTreeModel.this.recalculateCache(parent);

      fireTreeStructureChanged(this, e.getPath(), null, null);
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
      for (Object o : e.getChildren()) {
        sortingCache.remove(o);
      }

      // get sorted indices
      Object parent = e.getTreePath().getLastPathComponent();
      List<Object> list = sortingCache.get(parent);
      int[] newIndices = new int[e.getChildIndices().length];
      if (list != null) {
        for (int i = 0; i < e.getChildIndices().length; i++) {
          newIndices[i] = list.indexOf(e.getChildIndices()[i]);
        }
      }

      // forward event with adjusted indices
      fireTreeNodesRemoved(this, e.getPath(), newIndices, e.getChildren());
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
      Object parent = e.getTreePath().getLastPathComponent();
      SortedTreeModel.this.recalculateCache(parent);

      // get sorted indices
      List<Object> list = sortingCache.get(parent);
      int[] newIndices = new int[e.getChildIndices().length];
      if (list != null) {
        for (int i = 0; i < e.getChildIndices().length; i++) {
          newIndices[i] = list.indexOf(e.getChildIndices()[i]);
        }
      }

      fireTreeNodesInserted(this, e.getPath(), newIndices, e.getChildren());
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
      Object parent = e.getTreePath().getLastPathComponent();
      SortedTreeModel.this.recalculateCache(parent);

      // get sorted indices
      List<Object> list = sortingCache.get(parent);
      int[] newIndices = new int[e.getChildIndices().length];
      if (list != null) {
        for (int i = 0; i < e.getChildIndices().length; i++) {
          newIndices[i] = list.indexOf(e.getChildIndices()[i]);
        }
      }
      fireTreeNodesChanged(this, e.getPath(), newIndices, e.getChildren());
    }
  };

  /**
   * Instantiates a new sorted tree model.
   * 
   * @param model
   *          the model to sort
   */
  public SortedTreeModel(TreeModel model) {
    treeModel = model;
    // add tree listener
    treeModel.addTreeModelListener(listener);
    recalculateCache(getRoot());
  }

  /**
   * Helper method to recalculate the cache.
   * 
   * @param parent
   *          the parent
   */
  private void recalculateCache(Object parent) {
    sortingCache.remove(parent);

    List<Object> list = new ArrayList<>();

    for (int i = 0; i < treeModel.getChildCount(parent); i++) {
      Object node = treeModel.getChild(parent, i);
      list.add(node);
      if (!treeModel.isLeaf(node)) {
        recalculateCache(node);
      }
    }
    Collections.sort(list, comparator);

    sortingCache.put(parent, list);
  }

  /**
   * Gets the tree model.
   * 
   * @return the tree model
   */
  public TreeModel getTreeModel() {
    return treeModel;
  }

  @Override
  public Object getChild(Object parent, int index) {
    List<Object> list = sortingCache.get(parent);
    if (list != null && index < list.size()) {
      return list.get(index);
    }

    return null;
  }

  @Override
  public int getChildCount(Object parent) {
    List<Object> list = sortingCache.get(parent);
    if (list != null) {
      return list.size();
    }

    return 0;
  }

  @Override
  public int getIndexOfChild(Object parent, Object child) {
    List<Object> list = sortingCache.get(parent);
    if (list != null) {
      return list.indexOf(child);
    }

    return -1;
  }

  @Override
  public Object getRoot() {
    return treeModel.getRoot();
  }

  @Override
  public boolean isLeaf(Object node) {
    return treeModel.isLeaf(node);
  }

  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
    treeModel.valueForPathChanged(path, newValue);
  }

}
