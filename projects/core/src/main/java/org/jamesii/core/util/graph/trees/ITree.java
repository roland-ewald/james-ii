/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.trees;

import java.util.List;
import java.util.Map;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IGraph;

/**
 * Basic interface for all tree classes
 * 
 * @author Roland Ewald
 * 
 * @param <V>
 *          Type of vertices
 * @param <E>
 *          Type of edges
 */
public interface ITree<V, E extends Edge<V>> extends IGraph<V, E> {

  /**
   * Get children of a list of parents.
   * 
   * @param vertices
   *          the vertices
   * 
   * @return the children
   */
  List<V> getChildren(List<V> vertices);

  /**
   * Get children of vertex.
   * 
   * @param vertex
   *          the vertex
   * 
   * @return list with all children
   */
  List<V> getChildren(V vertex);

  /**
   * Get mapping from child to parent.
   * 
   * @return the child to parent map
   */
  Map<V, V> getChildToParentMap();

  /**
   * Return list of the leaves in the tree (ie., nodes that do not have a
   * parent).
   * 
   * @return the leaves
   */
  List<V> getLeaves();

  /**
   * Returns the root of this tree
   * 
   * @return root vertex
   */
  V getRoot();

  /**
   * Get all vertices in the complete subtree below the parent.
   * 
   * @param subtreeParent
   *          the subtree parent
   * 
   * @return the vertices of the subtree
   */
  List<V> getVerticesOfSubtree(V subtreeParent);

  /**
   * Gets all leaves from the subtree below the given parent.
   * 
   * @param parent
   *          the parent vertex of the subtree
   * 
   * @return the leaves of the subtree
   */
  List<V> getLeavesOfSubtree(V parent);

  /**
   * Checks whether given vertex is a leaf.
   * 
   * @param vertex
   *          the vertex to be checked
   * 
   * @return true, if it is a leaf
   */
  boolean isLeaf(V vertex);
}