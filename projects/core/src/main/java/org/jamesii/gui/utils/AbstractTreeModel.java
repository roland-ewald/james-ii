/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import org.jamesii.SimSystem;
import org.jamesii.core.util.collection.ListenerSupport;

/**
 * Abstract class for implementing {@link TreeModel} which already implements a
 * couple of methods and provides helpful protected methods.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractTreeModel implements TreeModel {
  /**
   * The listeners.
   */
  private final ListenerSupport<TreeModelListener> listeners =
      new ListenerSupport<>();

  @Override
  public final synchronized void addTreeModelListener(TreeModelListener l) {
    getListeners().addListener(l);
  }

  @Override
  public final synchronized void removeTreeModelListener(TreeModelListener l) {
    getListeners().removeListener(l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created using the parameters
   * passed into the fire method.
   * 
   * @param source
   *          the node being changed
   * @param path
   *          the path to the root node
   * @param childIndices
   *          the indices of the changed elements
   * @param children
   *          the changed elements
   */
  protected final synchronized void fireTreeNodesChanged(Object source,
      Object[] path, int[] childIndices, Object[] children) {
    TreeModelEvent e = null;

    for (TreeModelListener l : getListeners()) {
      if (l != null) {
        if (e == null) {
          e = new TreeModelEvent(source, path, childIndices, children);
        }
        l.treeNodesChanged(e);
      }
    }
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created using the parameters
   * passed into the fire method.
   * 
   * @param source
   *          the node where new elements are being inserted
   * @param path
   *          the path to the root node
   * @param childIndices
   *          the indices of the new elements
   * @param children
   *          the new elements
   */
  protected final synchronized void fireTreeNodesInserted(Object source,
      Object[] path, int[] childIndices, Object[] children) {
    TreeModelEvent e = null;
    for (TreeModelListener l : getListeners()) {
      if (l != null) {
        if (e == null) {
          e = new TreeModelEvent(source, path, childIndices, children);
        }
        l.treeNodesInserted(e);
      }
    }
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created using the parameters
   * passed into the fire method.
   * 
   * @param source
   *          the node where elements are being removed
   * @param path
   *          the path to the root node
   * @param childIndices
   *          the indices of the removed elements
   * @param children
   *          the removed elements
   * @see javax.swing.event.EventListenerList
   */
  protected final synchronized void fireTreeNodesRemoved(Object source,
      Object[] path, int[] childIndices, Object[] children) {
    TreeModelEvent e = null;
    for (TreeModelListener l : getListeners()) {
      if (l != null) {
        if (e == null) {
          e = new TreeModelEvent(source, path, childIndices, children);
        }
        l.treeNodesRemoved(e);
      }
    }
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created using the parameters
   * passed into the fire method.
   * 
   * @param source
   *          the node where the tree model has changed
   * @param path
   *          the path to the root node
   * @param childIndices
   *          the indices of the affected elements
   * @param children
   *          the affected elements
   * @see javax.swing.event.EventListenerList
   */
  protected final synchronized void fireTreeStructureChanged(Object source,
      Object[] path, int[] childIndices, Object[] children) {
    TreeModelEvent e = null;
    for (TreeModelListener l : getListeners()) {
      if (l != null) {
        if (e == null) {
          e = new TreeModelEvent(source, path, childIndices, children);
        }
        l.treeStructureChanged(e);
      }
    }
  }

  /**
   * Gets the path to node. A depth first search is performed. Use with caution
   * when there are cycles within the tree model.
   * 
   * @param node
   *          the node
   * @return the path to node or empty array if node not in tree
   */
  protected final synchronized Object[] getPathToNode(Object node) {
    Deque<Object> nodeStack = new ArrayDeque<>();
    nodeStack.push(getRoot());
    if (!node.equals(getRoot()) && !traverse(getRoot(), node, nodeStack)) {
      return new Object[0];
    }

    return nodeStack.toArray();
  }

  /**
   * Helper method for {@link #getPathToNode(Object)}.
   * 
   * @param node
   *          the node to traverse in
   * @param searchedNode
   *          the searched node
   * @param nodeStack
   *          the current node stack to find circles
   * @return true if node found in this node or its subnodes.
   */
  private boolean traverse(Object node, Object searchedNode,
      Deque<Object> nodeStack) {
    for (int i = 0; i < getChildCount(node); i++) {
      Object n = getChild(node, i);
      if (searchedNode.equals(n)) {
        nodeStack.push(searchedNode);
        return true;
      }

      // do not traverse if node is already in stack which means we have loops
      // in tree
      if (nodeStack.contains(n)) {
        SimSystem.report(Level.WARNING, "Stack already contains " + n);
        continue;
      }
      nodeStack.push(n);
      if (!isLeaf(n) && traverse(n, searchedNode, nodeStack)) {
        return true;
      }
      nodeStack.pop();
    }

    return false;
  }

  /**
   * @return the listeners
   */
  public ListenerSupport<TreeModelListener> getListeners() {
    return listeners;
  }
}
