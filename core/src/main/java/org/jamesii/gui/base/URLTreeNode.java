/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

/**
 * Tree node that is used by the {@link URLTreeModel} to represent nodes in its
 * tree structure. This node is able to sort its children according to applied
 * placements and it can also hold an arbitrary by generic parameter specified
 * object.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          specifies the type of object the tree node can store
 * 
 */
public class URLTreeNode<E> implements TreeNode {
  /**
   * children of this node
   */
  private List<URLTreeNode<E>> children = new ArrayList<>();

  /**
   * this node's parent. null if none
   */
  private URLTreeNode<E> parent = null;

  /**
   * this nodes id in tree
   */
  private String id;

  /**
   * stores the placement objects associated with each child node
   */
  private final Map<URLTreeNode<E>, URLTreeNodePlacement> placement =
      new HashMap<>();

  /**
   * stores the attached object
   */
  private E attachment;

  /**
   * Creates a new node with the specified id and the specified parent
   * 
   * @param id
   *          the nodes id
   * @param parent1
   *          the parent
   */
  public URLTreeNode(String id, URLTreeNode<E> parent1) {
    this(id, parent1, null);
  }

  /**
   * Creates a new node with the specified id, the specified parent and the
   * specified attachement object
   * 
   * @param id
   *          the nodes id
   * @param parent
   *          the parent
   * @param attachment
   *          the object to attach to this node
   */
  public URLTreeNode(String id, URLTreeNode<E> parent, E attachment) {
    this.attachment = attachment;
    this.parent = parent;
    this.id = id;
  }

  /**
   * @return the attached object if any
   */
  public E getAttachedObject() {
    return attachment;
  }

  /**
   * Sets the attached object. Replaces the existing one.
   * 
   * @param o
   *          the object to attach
   */
  public void setAttachedObject(E o) {
    // E old=attachment;
    attachment = o;
  }

  /**
   * @return the node's id
   */
  public String getId() {
    return id;
  }

  @Override
  public Enumeration<URLTreeNode<E>> children() {
    return new IteratorEnumeration<>(children.iterator());
  }

  @Override
  public boolean getAllowsChildren() {
    return true;
  }

  @Override
  public TreeNode getChildAt(int childIndex) {
    return children.get(childIndex);
  }

  @Override
  public int getChildCount() {
    return children.size();
  }

  @Override
  public int getIndex(TreeNode node) {
    return children.indexOf(node);
  }

  @Override
  public TreeNode getParent() {
    return parent;
  }

  @Override
  public boolean isLeaf() {
    return children.size() == 0;
  }

  /**
   * Adds a node to this node using the specified placement modifier.
   * 
   * @param node
   *          the node to add
   * @param p
   *          the placement modifier to be used
   * @return node that was updated or added
   */
  public URLTreeNode<E> addNode(URLTreeNode<E> node, URLTreeNodePlacement p) {
    for (URLTreeNode<E> n : children) {
      if (n.getId().equals(node.getId())) {
        n.setAttachedObject(node.getAttachedObject());
        setPlacement(n, p);
        sortNodes();
        return n;
      }
    }

    node.setParent(this);
    children.add(node);
    setPlacement(node, p);
    sortNodes();
    return node;
  }

  /**
   * Sets the parent node
   * 
   * @param treeNode
   *          the parent node
   */
  private void setParent(URLTreeNode<E> treeNode) {
    this.parent = treeNode;
  }

  /**
   * Sets the placement for a given node.
   * 
   * @param node
   *          the node
   * @param p
   *          the placement for given node
   */
  private void setPlacement(URLTreeNode<E> node, URLTreeNodePlacement p) {
    placement.put(node, p);
    sortNodes();
  }

  /**
   * @param node
   *          the node the placement is needed for
   * @return the nodes placement modifier
   */
  public URLTreeNodePlacement getPlacement(URLTreeNode<E> node) {
    return placement.get(node);
  }

  @Override
  public String toString() {
    if (parent == null || parent.placement.get(this) == null) {
      return id;
    }
    return String.format("%s (%s)", id, parent.placement.get(this));
  }

  /**
   * helper function that is called after a node is added to maintain a close
   * approximation of the desired order of nodes.
   * <p>
   * Note: the resulting order not might always represent the desired order
   */
  private void sortNodes() {
    List<URLTreeNode<E>> sorted = new ArrayList<>();
    List<URLTreeNode<E>> toDo = new ArrayList<>();

    for (URLTreeNode<E> a : children) {
      URLTreeNodePlacement p = placement.get(a);

      if (!insertInto(sorted, a, p)) {
        toDo.add(a);
      }
    }

    while (toDo.size() > 0) {
      int lastSize = toDo.size();
      for (int i = toDo.size() - 1; i >= 0; i--) {
        if (insertInto(sorted, toDo.get(i), placement.get(toDo.get(i)))) {
          toDo.remove(i);
        }
      }

      // if in the last step no item could be added due to placement
      // restrictions try to solve it by
      // removing one placement modifier at a time
      if (lastSize == toDo.size()) {
        // to solve this conflict try to add one element from toDo without
        // placement modifier and then
        // try again with the remaining elements
        insertInto(sorted, toDo.get(toDo.size() - 1), null);
        toDo.remove(toDo.size() - 1);
      }
    }

    children.clear();
    children = sorted;

  }

  /**
   * Helper function that is used by the {@link #sortNodes} method. It inserts a
   * node in another list of nodes according to the placement modifier.
   * 
   * @param sorted
   *          the list of already sorted nodes
   * @param node
   *          the node to insert
   * @param p
   *          the placement modifier used to determine the positon of the node
   *          to insert
   * @return true if the node could be inserted using the specified placement
   *         modifier
   *         <p>
   *         it might return false e.g. in case the node is to be placed after
   *         another not yet added node
   */
  private boolean insertInto(List<URLTreeNode<E>> sorted, URLTreeNode<E> node,
      URLTreeNodePlacement p) {
    if (p == null) {
      // add before the entries with END and LAST
      for (int i = 0; i < sorted.size(); i++) {
        URLTreeNodePlacement pTMP = placement.get(sorted.get(i));
        if (pTMP != null && (pTMP.isEnd() || pTMP.isLast())) {
          sorted.add(i, node);
          return true;
        }
      }
      sorted.add(node);
    } else {
      if (p.isFirst()) {
        sorted.add(0, node);
      }
      if (p.isLast()) {
        sorted.add(node);
      }
      if (p.isStart()) {
        // find last first actions
        for (int i = 0; i < sorted.size(); i++) {
          URLTreeNodePlacement pTMP = placement.get(sorted.get(i));
          if (pTMP == null || (!pTMP.isFirst() && !pTMP.isStart())) {
            sorted.add(i, node);
            return true;
          }
        }
        sorted.add(node);
      }
      if (p.isEnd()) {
        // find first last action
        for (int i = sorted.size() - 1; i >= 0; i--) {
          URLTreeNodePlacement pTMP = placement.get(sorted.get(i));
          if (pTMP == null || !pTMP.isLast()) {
            sorted.add(i + 1, node);
            return true;
          }
        }
        sorted.add(node);
      }
      if (p.isAfter()) {
        // try to find the action this action is to be placed after
        for (int i = 0; i < sorted.size(); i++) {
          if (sorted.get(i).getId().equals(p.getNodeId())) {
            sorted.add(i + 1, node);
            return true;
          }
        }
        return false;
      }

      if (p.isBefore()) {
        // try to find the action this action is to be placed before
        for (int i = 0; i < sorted.size(); i++) {
          if (sorted.get(i).getId().equals(p.getNodeId())) {
            sorted.add(i, node);
            return true;
          }
        }
        return false;
      }
    }
    return true;
  }

  /**
   * Removes a given node if it is a child of this node
   * 
   * @param node
   *          the node to remove
   */
  public final void remove(URLTreeNode<?> node) {
    int i = children.indexOf(node);
    remove(i);
    // sortNodes();
  }

  /**
   * Removes the child node specified by {@code index}
   * 
   * @param index
   *          the child's index
   */
  public void remove(int index) {
    if (index >= 0 && index < children.size()) {
      URLTreeNode<E> removed = children.remove(index);
      placement.remove(removed);
    }
  }

}
