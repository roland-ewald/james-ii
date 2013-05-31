/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.Iterator;
import java.util.List;

import org.jamesii.core.util.graph.trees.ITree;

/**
 * Provides checks for graph equality.
 * 
 * @author Roland Ewald
 */
public final class EqualsCheck {

  /**
   * Should not be instantiated.
   */
  private EqualsCheck() {
  }

  /**
   * Checks two tree for equality, which means that they have the same structure
   * and all their vertices are equal (w.r.t. {@link Object#equals(Object)}).
   * 
   * @param tree1
   *          first tree
   * @param tree2
   *          second tree
   * 
   * @param <V>
   *          type of vertices
   * @param <E>
   *          type of edges
   * 
   * @return true, if both trees are equal
   */
  public static <V, E extends Edge<V>> boolean equals(ITree<V, E> tree1,
      ITree<V, E> tree2) {

    return EqualsCheck.recursiveTreeCheck(tree1, tree1.getRoot(), tree2,
        tree2.getRoot());
  }

  /**
   * Recursively checks for tree equality (structural and w.r.t. values).
   * 
   * @param tree1
   *          first tree
   * @param tree1Node
   *          current node of first tree (start with root)
   * @param tree2
   *          second tree
   * @param tree2Node
   *          current node of second tree (start with root)
   * @param <V>
   *          type of vertices
   * @param <E>
   *          type of edges
   * @return true if trees are equal, otherwise false
   */
  protected static <V, E extends Edge<V>> boolean recursiveTreeCheck(
      ITree<V, E> tree1, V tree1Node, ITree<V, E> tree2, V tree2Node) {

    if (!tree1Node.equals(tree2Node)) {
      return false;
    }

    List<V> childNodesTree1 = tree1.getChildren(tree1Node);
    List<V> childNodesTree2 = tree2.getChildren(tree2Node);

    // Check if both are null (then they're equal) or only one (then they're
    // not)
    if (childNodesTree1 == null && childNodesTree2 == null) {
      return true;
    }
    if (childNodesTree1 == null || childNodesTree2 == null) {
      return false;
    }

    if (childNodesTree1.size() != childNodesTree2.size()) {
      return false;
    }

    Iterator<V> cnt1it = childNodesTree1.iterator();
    Iterator<V> cnt2it = childNodesTree2.iterator();
    while (cnt1it.hasNext()) {
      if (!recursiveTreeCheck(tree1, cnt1it.next(), tree2, cnt2it.next())) {
        return false;
      }
    }

    return true;
  }

}
