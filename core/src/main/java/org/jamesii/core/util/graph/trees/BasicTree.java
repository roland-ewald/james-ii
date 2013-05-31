/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.graph.BasicGraph;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.traverse.CycleDetection;

/**
 * Base class for all trees. Merely stores the root vertex, allows to get and
 * set it, and provides a link to the {@link CycleDetection}. The
 * {@link CycleDetection} allows to test if the tree is indeed a tree. This is
 * computationally expensive and should therefore be used with caution.
 * 
 * @param <V>
 *          type of vertices
 * @param <E>
 *          type of edges
 * 
 * @author Roland Ewald
 */
public abstract class BasicTree<V, E extends Edge<V>> extends BasicGraph<V, E>
    implements ITree<V, E> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7133091964325784502L;

  /** Root of the tree. */
  private V treeRoot = null;

  /**
   * Default constructor.
   */
  public BasicTree() {
    super();
    super.setSimple(true);
  }

  @Override
  public V getRoot() {
    return this.treeRoot;
  }

  @Override
  public List<V> getChildren(List<V> parentVertices) {
    List<V> returnList = new ArrayList<>();
    for (V pv : parentVertices) {
      returnList.addAll(getChildren(pv));
    }
    return returnList;
  }

  @Override
  public List<V> getLeavesOfSubtree(V parent) {

    List<V> leaves = new ArrayList<>();

    if (isLeaf(parent)) {
      leaves.add(parent);
      return leaves;
    }

    for (V currrentChild : getChildren(parent)) {
      leaves.addAll(getLeavesOfSubtree(currrentChild));
    }

    return leaves;
  }

  @Override
  public List<V> getVerticesOfSubtree(V subtreeParent) {
    List<V> returnList = new ArrayList<>();
    List<V> currentChildren = getChildren(subtreeParent);
    while (currentChildren.size() > 0) {
      returnList.addAll(currentChildren);
      currentChildren = getChildren(currentChildren);
    }
    return returnList;
  }

  @Override
  public boolean isDirected() {
    return true;
  }

  @Override
  public void setSimple(boolean simple) {
    if (!simple) {
      throw new IllegalArgumentException(
          "BasicTree.java: Trees with multi-edges are not supported!");
    }
  }

  /**
   * Gets the tree root.
   * 
   * @return the tree root
   */
  public V getTreeRoot() {
    return treeRoot;
  }

  /**
   * Specifies which node of this graph is the root. Will only work if the graph
   * is a tree.
   * 
   * @param treeRoot
   *          the tree root
   */
  public void setTreeRoot(V treeRoot) {
    this.treeRoot = treeRoot;
  }

  /**
   * Test for tree property.
   * 
   * @return true, if graph is a tree (i.e., there are no cycles)
   */
  protected boolean testForTree() {
    return CycleDetection.detectCycle(this).isEmpty();
  }

  /**
   * Gets the vertex sequence from root to given vertex.
   * 
   * @param childToParentMap
   *          the child to parent map
   * @param vertex
   *          the destination vertex
   * 
   * @return the vertex sequence from root
   */
  public static <V> List<V> getVertexSequenceFromRoot(
      Map<V, V> childToParentMap, V vertex) {
    List<V> vertexSequenceFromRoot = new ArrayList<>();
    V currentVertex = vertex;
    while (childToParentMap.get(currentVertex) != null) {
      currentVertex = childToParentMap.get(currentVertex);
      vertexSequenceFromRoot.add(0, currentVertex);
    }
    return vertexSequenceFromRoot;
  }

  /**
   * Gets the vertex sequence from root to a destination vertex. If called
   * often, you may consider generating the map from children to parents just
   * once and then use the static method (depending on the implementation).
   * 
   * @param vertex
   *          the destination vertex
   * 
   * @return the vertex sequence from root
   */
  public List<V> getVertexSequenceFromRoot(V vertex) {
    return getVertexSequenceFromRoot(getChildToParentMap(), vertex);
  }
}
