/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.trees.ITree;

/**
 * This is a {@link JTree} implementation to display instances of {@link ITree}
 * conveniently.
 * 
 * @param <V>
 *          the type of vertices
 * @author Roland Ewald
 */
public class JTreeView<V> extends JTree {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1442919814124121192L;

  /** The tree. */
  private final ITree<V, ? extends Edge<V>> tree;

  /**
   * Default constructor.
   * 
   * @param tr
   *          the tree to be viewed
   */
  public JTreeView(ITree<V, ? extends Edge<V>> tr) {
    super();
    tree = tr;
    setModel(new DefaultTreeModel(createTreeNodes(tree.getRoot())));
  }

  /**
   * Recursively creates {@link MutableTreeNode} instances for all elements in
   * the SelectionTreeSetDefinition}.
   * 
   * @param currentNode
   *          the current node
   * 
   * @return the associated {@link MutableTreeNode} instance
   */
  protected MutableTreeNode createTreeNodes(V currentNode) {
    DefaultMutableTreeNode uiNode = new DefaultMutableTreeNode(currentNode);
    decorate(uiNode, currentNode);
    for (V child : tree.getChildren(currentNode)) {
      uiNode.add(createTreeNodes(child));
    }
    return uiNode;
  }

  /**
   * Override this to decorate the nodes.
   * 
   * @param uiNode
   *          the user interface node
   * @param currentNode
   *          the node in the tree
   */
  protected void decorate(DefaultMutableTreeNode uiNode, V currentNode) {
  }

  @Override
  public DefaultTreeModel getModel() {
    return (DefaultTreeModel) super.getModel();
  }

}
