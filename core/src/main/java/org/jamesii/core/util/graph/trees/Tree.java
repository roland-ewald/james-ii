/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.graph.Edge;

/**
 * Basic class for trees. All vertices are stored in an array list. Each vertex
 * (except for the root) stores the edge to its parent in the mapping
 * {@link Tree#edges}. For faster look-up, there is also a list
 * {@link Tree#childNodes} that associates a parent vertex (as a key) with a
 * list of its children.
 * 
 * Date: date 11.12.2006
 * 
 * @param <V>
 *          Type of vertices
 * @param <E>
 *          Type of edges
 * 
 * @author Roland Ewald
 */
public class Tree<V, E extends Edge<V>> extends BasicTree<V, E> {
  static {
    SerialisationUtils.addDelegateForConstructor(Tree.class,
        new IConstructorParameterProvider<Tree<?, ?>>() {
          @Override
          public Object[] getParameters(Tree<?, ?> tree) {
            Object[] params =
                new Object[] { tree.getVertices(), tree.getVEMap() };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 5323426595239499503L;

  /** Checks whether this graph is still a tree. */
  private boolean checkForTree = true;

  /** List of all child nodes. */
  private final Map<V, List<V>> childNodes = new HashMap<>();

  /** Data structure to store the edges of the tree (child => edge to parent). */
  private final Map<V, E> edges = new HashMap<>();

  /** Data structure to store the vertex objects in a list. */
  private final List<V> vertices = new ArrayList<>();

  /**
   * Default constructor.
   * 
   * @param verts
   *          the vertices
   */
  public Tree(List<V> verts) {
    for (V vertex : verts) {
      vertices.add(vertex);
      childNodes.put(vertex, new ArrayList<V>());
    }
  }

  /**
   * Instantiates a new tree.
   * 
   * @param verts
   *          the vertices
   * @param edgeMap
   *          the map vertex => edge
   */
  public Tree(List<V> verts, Map<V, E> edgeMap) {
    this(verts);
    for (Entry<V, E> edgeEntry : edgeMap.entrySet()) {
      if (childNodes.containsKey(edgeEntry.getKey())) {
        addEdge(edgeEntry.getValue());
      } else {
        SimSystem.report(Level.WARNING,
            "Could not find vertex '" + edgeEntry.getKey()
                + "' in tree, ignoring edge '" + edgeEntry.getValue() + "'.");
      }
    }
  }

  /**
   * The child is assumed to be the first node, the parent the second one.
   */
  @Override
  public boolean addEdge(E edge) {

    boolean success = addEdgeInternally(edge);

    if (success && checkForTree && !testForTree()) {
      removeEdge(edge);
      success = false;
    }

    return success;
  }

  /**
   * Adds an edge between child (first node) and parent (second node).
   * 
   * @param edge
   *          edge to be added
   * 
   * @return true, if adds the edge internally
   */
  protected boolean addEdgeInternally(E edge) {

    E edgeToParent = edges.get(edge.getFirstVertex());
    boolean success = (edgeToParent == null);

    if (success) {
      edges.put(edge.getFirstVertex(), edge);
      childNodes.get(edge.getSecondVertex()).add(edge.getFirstVertex());
    }

    return success;
  }

  @Override
  public void addVertex(V vertex) {
    vertices.add(vertex);
    childNodes.put(vertex, new ArrayList<V>());
  }

  @Override
  public void addVertices(V[] vertexList) {
    for (V v : vertexList) {
      addVertex(v);
    }
  }

  @Override
  public List<V> getChildren(V vertex) {
    return childNodes.get(vertex);
  }

  /**
   * Gets the child to parent map.
   * 
   * @return the child to parent map
   * 
   * @see org.jamesii.core.util.graph.trees.ITree#getChildToParentMap()
   */
  @Override
  public Map<V, V> getChildToParentMap() {

    Map<V, V> returnMap = new HashMap<>();

    for (Entry<V, E> edgeToParent : edges.entrySet()) {
      returnMap.put(edgeToParent.getKey(), edgeToParent.getValue()
          .getSecondVertex());
    }

    return returnMap;
  }

  @Override
  public Collection<E> getEdges(V vertex) {
    Collection<E> returnEdges = new ArrayList<>();
    E edgeToParent = edges.get(vertex);

    if (edgeToParent != null) {
      returnEdges.add(edgeToParent);
    }

    List<V> children = childNodes.get(vertex);
    for (V child : children) {
      returnEdges.add(edges.get(child));
    }

    return returnEdges;
  }

  @Override
  public List<V> getLeaves() {

    Map<V, Boolean> isParent = new HashMap<>();

    Set<Entry<V, E>> entries = edges.entrySet();

    for (Entry<V, E> entry : entries) {
      isParent.put(entry.getValue().getSecondVertex(), true);
    }

    List<V> leaves = new ArrayList<>();

    for (V v : vertices) {
      if (isParent.get(v) == null) {
        leaves.add(v);
      }
    }

    return leaves;
  }

  @Override
  public List<V> getNeighboursOfNode(V vertex) {

    // add child nodes
    List<V> neighbours = new ArrayList<>(childNodes.get(vertex));

    // add parent
    E edgeToParent = edges.get(vertex);
    if (edgeToParent != null) {
      neighbours.add(edgeToParent.getSecondVertex());
    }

    return neighbours;
  }

  @Override
  public int getVertexCount() {
    return vertices.size();
  }

  @Override
  public List<V> getVertices() {
    return new ArrayList<>(vertices);
  }

  @Override
  public boolean hasEdge(V v1, V v2) {
    return (edges.get(v2).getSecondVertex().equals(v1) || edges.get(v1)
        .getSecondVertex().equals(v2));
  }

  @Override
  public boolean hasVertex(V vertex) {
    return childNodes.get(vertex) != null;
  }

  /**
   * Get status of tree checking.
   * 
   * @return true, if checks if is tree checked
   */
  public boolean isTreeChecked() {
    return this.checkForTree;
  }

  @Override
  public boolean removeEdge(E edge) {
    E edgeToBeFound = edges.remove(edge.getFirstVertex());
    boolean success = (edgeToBeFound != null);

    if (success) {
      childNodes.get(edgeToBeFound.getSecondVertex()).remove(
          edge.getFirstVertex());
    }

    return success;
  }

  @Override
  public boolean removeVertex(V vertex) {
    E edgeToParent = edges.remove(vertex);
    boolean success = (edgeToParent != null) && vertices.remove(vertex);
    if (success) {
      childNodes.get(edgeToParent.getSecondVertex()).remove(vertex);
      List<V> children = childNodes.remove(vertex);
      for (V v : children) {
        edges.remove(v);
      }
    }
    return success;
  }

  @Override
  public boolean removeVertices(V[] vertexList) {
    for (V v : vertexList) {
      removeVertex(v);
    }
    return true;
  }

  /**
   * Turn circle detection off (useful when adding multiple edges).
   */
  public void turnTreeCheckOff() {
    this.checkForTree = false;
  }

  /**
   * Turn circle detection on (checking is done once, returns false and refuses
   * switching to on (@see {@link Tree#isTreeChecked()}) if this is no tree
   * anymore).
   * 
   * @return true, if graph has passed the tree test (and the testing is
   *         switched on)
   */
  public boolean turnTreeCheckOn() {
    this.checkForTree = testForTree();
    return this.checkForTree;
  }

  /**
   * Checks if is check for tree.
   * 
   * @return true, if is check for tree
   */
  public boolean isCheckForTree() {
    return checkForTree;
  }

  /**
   * Sets the check for tree.
   * 
   * @param checkForTree
   *          the new check for tree
   */
  public void setCheckForTree(boolean checkForTree) {
    this.checkForTree = checkForTree;
  }

  /**
   * Gets the child nodes.
   * 
   * @return the child nodes
   */
  public Map<V, List<V>> getChildNodes() {
    return childNodes;
  }

  /**
   * Returns the mapping between vertices and their edges (to the corresponding
   * parent).
   * 
   * @return the edges
   */
  public Map<V, E> getVEMap() {
    return new HashMap<>(edges);
  }

  /**
   * Sets the vertices.
   * 
   * @param vertices
   *          the new vertices
   */
  public void setVertices(List<V> vertices) {
    for (V v : vertices) {
      removeVertex(v);
    }
    this.vertices.addAll(vertices);
  }

  @Override
  public Collection<E> getEdges() {
    return new ArrayList<>(edges.values());
  }

  @Override
  public boolean isLeaf(V vertex) {
    if (!childNodes.containsKey(vertex)) {
      throw new IllegalArgumentException("Vertex '" + vertex
          + "' is not part of the tree.");
    }
    return getChildren(vertex).size() == 0;
  }

}